package by.bashlikovvv.bluetooth.action

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import by.bashlikovv.bluetooth.util.CryptoUtils
import by.bashlikovv.bluetooth.util.ECDH_B163
import by.bashlikovvv.bluetooth.devices.huami.Huami2021ChunkedDecoder
import by.bashlikovvv.bluetooth.devices.huami.Huami2021ChunkedEncoder
import by.bashlikovvv.bluetooth.devices.huami.Huami2021Handler
import by.bashlikovvv.bluetooth.devices.huami.HuamiSupport
import by.bashlikovvv.bluetooth.model.GBDevice
import by.bashlikovvv.bluetooth.transactioin.TransactionBuilder
import java.util.Random
import java.util.UUID
import kotlin.experimental.xor

class InitOperation2021 : InitOperation, Huami2021Handler {
    private val huami2021ChunkedEncoder: Huami2021ChunkedEncoder?

    private val huami2021ChunkedDecoder: Huami2021ChunkedDecoder?

    private val privateEC = ByteArray(24)

    private var publicEC: ByteArray? = null

    private val remoteRandom = ByteArray(16)

    private val remotePublicEC = ByteArray(48)

    private var sharedEC: ByteArray? = null

    private val finalSharedSessionAES = ByteArray(16)

    constructor(
        needsAuth: Boolean,
        authFlags: Byte,
        cryptFlags: Byte,
        authKey: String,
        support: HuamiSupport,
        builder: TransactionBuilder,
        huami2021ChunkedEncoder: Huami2021ChunkedEncoder?,
        huami2021ChunkedDecoder: Huami2021ChunkedDecoder?,
    ) : super(
        needsAuth = needsAuth,
        authFlags = authFlags,
        cryptFlags = cryptFlags,
        authKey = authKey,
        support = support,
        builder = builder
    ) {
        this.huami2021ChunkedEncoder = huami2021ChunkedEncoder
        this.huami2021ChunkedDecoder = huami2021ChunkedDecoder
    }

    override fun doPerform() {
        support.enableNotifications(builder, true)
        builder.add(SetDeviceStateAction(device, GBDevice.State.INITIALIZING))
        generateKeyPair()
        val sendPubKeyCommand = ByteArray(48 + 4)
        sendPubKeyCommand[0] = 0x04
        sendPubKeyCommand[1] = 0x02
        sendPubKeyCommand[2] = 0x00
        sendPubKeyCommand[3] = 0x02
        System.arraycopy(publicEC!!, 0, sendPubKeyCommand, 4, 48)
        huami2021ChunkedEncoder?.write(
            builder,
            CHUNKED2021_ENDPOINT_AUTH,
            sendPubKeyCommand,
            true,
            false
        )
    }

    override fun onCharacteristicChanged(
        gatt: BluetoothGatt?,
        characteristic: BluetoothGattCharacteristic?
    ): Boolean {
        val characteristicUUID = characteristic?.uuid
        if (UUID_CHARACTERISTIC_CHUNKEDTRANSFER_2021_READ != characteristicUUID) {
            return super.onCharacteristicChanged(gatt, characteristic)
        }

        val value = characteristic.value
        if (value.size <= 1 || value.first() != (0x03).toByte()) {
            return super.onCharacteristicChanged(gatt, characteristic)
        }

        val needsAsk = huami2021ChunkedDecoder?.decode(value)
        if (needsAsk == true) {
            support.sendChunkedAck()
        }

        return true
    }

    private fun generateKeyPair() {
        val random = Random()
        random.nextBytes(privateEC)
        publicEC = ECDH_B163.ecdh_generate_public(privateEC)
    }

    override fun handle2021Payload(type: Short, payload: ByteArray) {
        if (type != CHUNKED2021_ENDPOINT_AUTH) {
            support.handle2021Payload(type, payload)
            return
        }

        if (payload.first() == RESPONSE && payload[1] == (0x04).toByte() && payload[2] == SUCCESS) {
            System.arraycopy(payload, 3, remoteRandom, 0, 16)
            System.arraycopy(payload, 19, remotePublicEC, 0, 48)
            sharedEC = ECDH_B163.ecdh_generate_shared(privateEC, remotePublicEC)
            val encryptedSequenceNumber =
                (sharedEC!![0].toInt() and 0xFF) or ((sharedEC!![1].toInt() and 0xFF) shl 8) or ((sharedEC!![2].toInt() and 0xFF) shl 16) or ((sharedEC!![3].toInt() and 0xFF) shl 24)
            val secretKey = getSecretKey()
            for (i in 0 until 16) {
                finalSharedSessionAES[i] = (sharedEC!![i + 8] xor secretKey[i])
            }

            huami2021ChunkedEncoder?.setEncryptionParameters(encryptedSequenceNumber, finalSharedSessionAES)
            huami2021ChunkedDecoder?.setEncryptionParameters(finalSharedSessionAES)

            try {
                val encryptedRandom1 = CryptoUtils.encryptAES(remoteRandom, secretKey)
                val encryptedRandom2 = CryptoUtils.encryptAES(remoteRandom, finalSharedSessionAES)
                if (encryptedRandom1.size == 16 && encryptedRandom2.size == 16) {
                    val command = ByteArray(33)
                    command[0] = 0x05
                    System.arraycopy(encryptedRandom1, 0, command, 1, 16)
                    System.arraycopy(encryptedRandom2, 0, command, 17, 16)
                    val builder = createTransactionBuilder("Sending double encryted random to device")
                    huami2021ChunkedEncoder?.write(builder, CHUNKED2021_ENDPOINT_AUTH, command, true, false)
                    support.performImmediately(builder)
                }
            } catch (_: Exception) {
            }
        } else if (payload.first() == RESPONSE && payload[1] == (0x05).toByte() && payload[2] == SUCCESS) {
            try {
                val builder = createTransactionBuilder("Authenticated, now initialize phase 2")
                builder.add(SetDeviceStateAction(device, GBDevice.State.INITIALIZING))
                support.enableFurtherNotifications(builder, true)
                support.setCurrentTimeWithService(builder)
                builder.add(SetDeviceStateAction(device, GBDevice.State.INITIALIZED))
                support.performImmediately(builder)
            } catch (_: Exception) {
            }
        } else if (payload.first() == RESPONSE && payload[1] == (0x05).toByte() && payload[2] == (0x25).toByte()) {
            val builder = createTransactionBuilder("Authentication failed")
            builder.add(SetDeviceStateAction(device, GBDevice.State.NOT_CONNECTED))
            support.performImmediately(builder)
        }
    }

    companion object {
        const val CHUNKED2021_ENDPOINT_AUTH: Short = 0x0082

        const val RESPONSE: Byte = 0x10

        const val SUCCESS: Byte = 0x01

        val UUID_CHARACTERISTIC_CHUNKEDTRANSFER_2021_READ =
            UUID.fromString("00000017-0000-3512-2118-0009af100700")
    }
}