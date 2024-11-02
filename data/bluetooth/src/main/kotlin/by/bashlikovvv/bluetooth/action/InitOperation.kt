package by.bashlikovvv.bluetooth.action

import android.annotation.SuppressLint
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import by.bashlikovvv.bluetooth.devices.huami.HuamiSupport
import by.bashlikovvv.bluetooth.model.AbstractBTLEOperation
import by.bashlikovvv.bluetooth.model.GBDevice
import by.bashlikovvv.bluetooth.transactioin.TransactionBuilder
import java.util.UUID
import kotlin.experimental.or
import java.lang.Character
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import kotlin.experimental.and

open class InitOperation : AbstractBTLEOperation<HuamiSupport> {
    protected val builder: TransactionBuilder

    private val needsAuth: Boolean

    private val authFlags: Byte

    private val cryptFlags: Byte

    private val authKey: String

    constructor(
        needsAuth: Boolean,
        authFlags: Byte,
        cryptFlags: Byte,
        authKey: String,
        support: HuamiSupport,
        builder: TransactionBuilder
    ) : super(support) {
        this.builder = builder
        this.needsAuth = needsAuth
        this.authFlags = authFlags
        this.cryptFlags = cryptFlags
        this.authKey = authKey
        builder.setCallback(this)
    }

    override fun doPerform() {
        support.enableNotifications(builder, true)
        if (needsAuth) {
            builder.add(SetDeviceStateAction(device, GBDevice.State.AUTHENTICATING))
            val sendKey = addAll(byteArrayOf(AUTH_SEND_KEY, authFlags), getSecretKey())
            builder.write(getCharacteristic(UUID_CHARACTERISTIC_AUTH), sendKey)
        } else {
            builder.add(SetDeviceStateAction(device, GBDevice.State.INITIALIZING))
            builder.write(getCharacteristic(UUID_CHARACTERISTIC_AUTH), requestAuthNumber())
        }
    }

    override fun onCharacteristicChanged(
        gatt: BluetoothGatt,
        characteristic: BluetoothGattCharacteristic
    ): Boolean {
        val characteristicUUID = characteristic.uuid
        if (UUID_CHARACTERISTIC_AUTH != characteristicUUID) {
            return super.onCharacteristicChanged(gatt, characteristic)
        }

        try {
            val value = characteristic.value
            if (value.first() == AUTH_RESPONSE) {
                return super.onCharacteristicChanged(gatt, characteristic)
            }

            if (value[1] == AUTH_SEND_KEY && value[2] == AUTH_SUCCESS) {
                val builder = createTransactionBuilder("Sending the secret key to the device")
                builder.write(characteristic, requestAuthNumber())
                support.performImmediately(builder)
            } else if ((value[1] and 0x0F) == AUTH_REQUEST_RANDOM_AUTH_NUMBER && value[2] == AUTH_SUCCESS) {
                val eValue = handleAESAuth(value, getSecretKey())
                val responseValue = byteArrayOf(
                    (AUTH_SEND_ENCRYPTED_AUTH_NUMBER or cryptFlags), authFlags, *eValue
                )

                val builder = createTransactionBuilder("Sending the encrypted random key to the device")
                builder.write(characteristic, responseValue)
                support.setCurrentTimeWithService(builder)
                support.performImmediately(builder)
            } else if ((value[1] and 0x0F) == AUTH_SEND_ENCRYPTED_AUTH_NUMBER) {
                if (value[2] == AUTH_SUCCESS) {
                    val builder = createTransactionBuilder("Authenticated, now initialize phase 2")
                    builder.add(SetDeviceStateAction(device, GBDevice.State.INITIALIZING))
                    support.enableFurtherNotifications(builder, true)
                    support.performImmediately(builder)
                } else if (value[2] == AUTH_FAIL) {
                    device.setState(GBDevice.State.NOT_CONNECTED)
                }
            } else {
                return super.onCharacteristicChanged(gatt, characteristic)
            }
        } catch (_: Exception) {
        }

        return true
    }

    private fun requestAuthNumber(): ByteArray {
        return if (cryptFlags == (0x00).toByte()) {
            byteArrayOf(AUTH_REQUEST_RANDOM_AUTH_NUMBER, authFlags)
        } else {
            byteArrayOf(
                cryptFlags or AUTH_REQUEST_RANDOM_AUTH_NUMBER, authFlags, 0x02, 0x01, 0x00
            )
        }
    }

    fun getSecretKey(authKey: String = this.authKey): ByteArray {
        val authKeyBytes = byteArrayOf(0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x40, 0x41, 0x42, 0x43, 0x44, 0x45)

        if (authKey.isNotEmpty()) {
            var srcBytes = authKey.trim().toByteArray()
            if (authKey.length == 34 && authKey.startsWith("0x")) {
                srcBytes = hexStringToByteArray(authKey.substring(2))
            }
            System.arraycopy(srcBytes, 0, authKeyBytes, 0, srcBytes.size.coerceAtMost(16))
        }

        return authKeyBytes
    }

    private fun addAll(array1: ByteArray, array2: ByteArray): ByteArray {
        val joinedArray = ByteArray(array1.size + array2.size)
        System.arraycopy(array1, 0, joinedArray, 0, array1.size)
        System.arraycopy(array2, 0, joinedArray, array1.size, array2.size)
        return joinedArray
    }

    private fun hexStringToByteArray(s: String): ByteArray {
        val len = s.length
        val data = ByteArray(len / 2)
        for (i in 0 until len step 2) {
            data[i / 2] = ((Character.digit(s[i], 16) shl 4) + Character.digit(s[i + 1], 16)).toByte()
        }
        return data
    }

    private fun handleAESAuth(value: ByteArray, secretKey: ByteArray): ByteArray {
        val mValue = value.copyOfRange(3, 19)
        val eCipher = @SuppressLint("GetInstance") Cipher.getInstance("AES/ECB/NoPadding")
        val newKey = SecretKeySpec(secretKey, "AES")
        eCipher.init(Cipher.ENCRYPT_MODE, newKey)
        return eCipher.doFinal(mValue)
    }

    override fun onConnectionStateChange(
        gatt: BluetoothGatt,
        status: Int,
        newState: Int
    ) {
        super.onConnectionStateChange(gatt, status, newState)
    }

    override fun onServicesDiscovered(gatt: BluetoothGatt) {
        super.onServicesDiscovered(gatt)
    }

    override fun onCharacteristicRead(
        gatt: BluetoothGatt,
        characteristic: BluetoothGattCharacteristic,
        status: Int
    ): Boolean = super.onCharacteristicRead(gatt, characteristic, status)

    override fun onCharacteristicWrite(
        gatt: BluetoothGatt,
        characteristic: BluetoothGattCharacteristic,
        status: Int
    ): Boolean = super.onCharacteristicWrite(gatt, characteristic, status)

    override fun onDescriptorRead(
        gatt: BluetoothGatt,
        descriptor: BluetoothGattDescriptor,
        status: Int
    ): Boolean = super.onDescriptorRead(gatt, descriptor, status)

    override fun onDescriptorWrite(
        gatt: BluetoothGatt,
        descriptor: BluetoothGattDescriptor,
        status: Int
    ): Boolean = super.onDescriptorWrite(gatt, descriptor, status)

    override fun onReadRemoteRssi(
        gatt: BluetoothGatt,
        rssi: Int,
        status: Int
    ) = super.onReadRemoteRssi(gatt, rssi, status)

    override fun onMtuChanged(
        gatt: BluetoothGatt,
        mtu: Int,
        status: Int
    ) = super.onMtuChanged(gatt, mtu, status)

    companion object {
        /**
         * Mi Band 2 authentication has three steps.
         * This is step 1: sending a "secret" key to the band.
         * This is byte 0, followed by {@link #AUTH_BYTE} and then the key.
         * In the response, it is byte 1 in the byte[] value.
         */
        const val AUTH_SEND_KEY: Byte = 0x01

        /**
         * Mi Band 2 authentication has three steps.
         * This is step 2: requesting a random authentication key from the band.
         * This is byte 0, followed by {@link #AUTH_BYTE}.
         * In the response, it is byte 1 in the byte[] value.
         */
        const val AUTH_REQUEST_RANDOM_AUTH_NUMBER: Byte = 0x02

        /**
         * Mi Band 2 authentication has three steps.
         * This is step 3: sending the encrypted random authentication key to the band.
         * This is byte 0, followed by {@link #AUTH_BYTE} and then the encrypted random authentication key.
         * In the response, it is byte 1 in the byte[] value.
         */
        const val AUTH_SEND_ENCRYPTED_AUTH_NUMBER: Byte = 0x03

        /**
         * Received in response to any authentication requests (byte 0 in the byte[] value.
         */
        const val AUTH_RESPONSE: Byte = 0x10

        /**
         * Received in response to any authentication requests (byte 2 in the byte[] value.
         * 0x01 means success.
         */
        const val AUTH_SUCCESS: Byte = 0x01

        /**
         * Received in response to any authentication requests (byte 2 in the byte[] value.
         * 0x04 means failure.
         */
        const val AUTH_FAIL: Byte = 0x04

        val UUID_CHARACTERISTIC_AUTH = UUID.fromString("00000009-0000-3512-2118-0009af100700")
    }
}