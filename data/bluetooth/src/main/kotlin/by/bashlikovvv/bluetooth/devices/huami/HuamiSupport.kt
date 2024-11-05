package by.bashlikovvv.bluetooth.devices.huami

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import by.bashlikovvv.bluetooth.action.InitOperation
import by.bashlikovvv.bluetooth.action.InitOperation2021
import by.bashlikovvv.bluetooth.action.SetDeviceStateAction
import by.bashlikovvv.bluetooth.model.AbstractBtLEDeviceSupport
import by.bashlikovvv.bluetooth.model.GBDevice
import by.bashlikovvv.bluetooth.model.QueueEntitiesProvider
import by.bashlikovvv.bluetooth.transactioin.TransactionBuilder
import java.io.IOException
import java.lang.Exception
import java.util.Calendar
import java.util.GregorianCalendar
import java.util.UUID
import java.util.concurrent.TimeUnit

abstract class HuamiSupport(
    private val key: String,
    device: GBDevice,
    provider: QueueEntitiesProvider,
) : AbstractBtLEDeviceSupport(device, provider), Huami2021Handler {
    protected open val authFlags: Byte = AUTH_BYTE

    private val cryptFlags: Byte = getCryptFlags()

    private var needsAuth: Boolean = true

    private var huami2021ChunkedDecoder: Huami2021ChunkedDecoder? = null

    private var huami2021ChunkedEncoder: Huami2021ChunkedEncoder? = null

    private var characteristicChunked2021Read: BluetoothGattCharacteristic? = null

    private var characteristicChunked2021Write: BluetoothGattCharacteristic? = null

    private var force2021protocol: Boolean = false

    override fun initializeDevice(builder: TransactionBuilder): TransactionBuilder {
        try {
            val authenticate = needsAuth && (cryptFlags == CRYPT_FLAGS)
            needsAuth = false
            characteristicChunked2021Read =
                getCharacteristic(UUID_CHARACTERISTIC_CHUNKED_TRANSFER_2021_READ)
            if (characteristicChunked2021Read != null && huami2021ChunkedDecoder == null) {
                huami2021ChunkedDecoder = Huami2021ChunkedDecoder(force2021protocol, this)
            }
            characteristicChunked2021Write =
                getCharacteristic(UUID_CHARACTERISTIC_CHUNKED_TRANSFER_2021_WRITE)
            if (characteristicChunked2021Write != null && huami2021ChunkedEncoder == null) {
                huami2021ChunkedEncoder = Huami2021ChunkedEncoder(
                    characteristicChunked2021Write!!, force2021protocol, getMtu()
                )
            }
            if (force2021protocol) {
                if (characteristicChunked2021Write != null && characteristicChunked2021Read != null) {
                    InitOperation2021(
                        needsAuth = authenticate,
                        authFlags = authFlags,
                        cryptFlags = cryptFlags,
                        support = this,
                        builder = builder,
                        authKey = key,
                        huami2021ChunkedEncoder = huami2021ChunkedEncoder,
                        huami2021ChunkedDecoder = huami2021ChunkedDecoder,
                    ).perform()
                }
            } else {
                InitOperation(
                    needsAuth = authenticate,
                    authFlags = authFlags,
                    cryptFlags = cryptFlags,
                    support = this,
                    builder = builder,
                    authKey = key,
                ).perform()
            }
            builder.add(SetDeviceStateAction(device, GBDevice.State.WAITING_FOR_RECONNECT))
        } catch (_: IOException) {
        }

        return builder
    }

    override fun onCharacteristicRead(
        gatt: BluetoothGatt,
        characteristic: BluetoothGattCharacteristic,
        status: Int
    ): Boolean  {
        return super.onCharacteristicRead(gatt, characteristic, status)
    }

    override fun onCharacteristicChanged(
        gatt: BluetoothGatt,
        characteristic: BluetoothGattCharacteristic
    ): Boolean {
        return if (characteristic.uuid == UUID_CHARACTERISTIC_AUTH) {
            true
        } else {
            super.onCharacteristicChanged(gatt, characteristic)
        }
    }

    protected open fun getCryptFlags(): Byte = CRYPT_FLAGS

    fun enableNotifications(
        builder: TransactionBuilder,
        enable: Boolean
    ): HuamiSupport {
        builder.notify(getCharacteristic(UUID_CHARACTERISTIC_NOTIFICATION), enable)
        builder.notify(getCharacteristic(UUID_CHARACTERISTIC_AUTH), enable)
//        builder.notify(getCharacteristic(UUID.fromString("00002a46-0000-1000-8000-00805f9b34fb")), enable)
        characteristicChunked2021Read?.let {
            builder.notify(characteristicChunked2021Read, enable)
        }

        return this
    }

    fun enableFurtherNotifications(builder: TransactionBuilder, enable: Boolean): HuamiSupport {
        builder.notify(getCharacteristic(UUID_CHARACTERISTIC_3_CONFIGURATION), enable)
        builder.notify(getCharacteristic(UUID_CHARACTERISTIC_6_BATTERY_INFO), enable)
        builder.notify(getCharacteristic(UUID_CHARACTERISTIC_AUDIO), enable)
        builder.notify(getCharacteristic(UUID_CHARACTERISTIC_AUDIO_DATA), enable)
        builder.notify(getCharacteristic(UUID_CHARACTERISTIC_DEVICE_EVENT), enable)
        builder.notify(getCharacteristic(UUID_CHARACTERISTIC_WORKOUT), enable)
        if (characteristicChunked2021Read != null) {
            builder.notify(characteristicChunked2021Read, enable)
        }

        return this
    }

    fun setCurrentTimeWithService(builder: TransactionBuilder): HuamiSupport {
        val calendar = GregorianCalendar()
        val bytes = getTimeBytes(calendar, TimeUnit.SECONDS)
        builder.write(getCharacteristic(UUID_CHARACTERISTIC_CURRENT_TIME), bytes)
        return this
    }

    fun sendChunkedAck() {
        val handle = huami2021ChunkedDecoder?.lastHandle
        val count = huami2021ChunkedDecoder?.lastCount

        try {
            val builder = createTransactionBuilder("send chunked ack")
            builder.write(characteristicChunked2021Read, byteArrayOf(0x04, 0x00, handle ?: 0, 0x01, count ?: 0))
        } catch (_: Exception) {
        }
    }

    protected fun truncateVibrationsOnOff(
        repeat: Short,
        onOffSequence: IntArray,
        limitMillis: Int,
    ): List<Short> {
        var totalLengthMs = 0

        // The on-off sequence, until the max total length is reached
        val onOff = mutableListOf<Short>()

        for (c in 0 until repeat) {
            for (i in onOffSequence.indices step 2) {
                val on = onOffSequence[i].toShort()
                val off = onOffSequence[i + 1].toShort()

                if (totalLengthMs + on + off > limitMillis) break

                onOff.add(on)
                onOff.add(off)
                totalLengthMs += on + off
            }
        }

        return onOff

    }

    private fun getTimeBytes(calendar: Calendar, precision: TimeUnit): ByteArray {
        var bytes = ByteArray(0)
        bytes = if (precision == TimeUnit.MINUTES) {
            shortCalendarToRawBytes(calendar)
        } else if (precision == TimeUnit.SECONDS) {
            calendarToRawBytes(calendar)
        } else {
            throw IllegalArgumentException()
        }
        val tail = byteArrayOf(0, mapTimeZone(calendar, TZ_FLAG_INCLUDE_DST_IN_TZ))

        return join(bytes, tail)
    }

    private fun join(start: ByteArray, end: ByteArray): ByteArray {
        val result = ByteArray(start.size + end.size)
        System.arraycopy(start, 0, result, 0, start.size)
        System.arraycopy(end, 0, result, start.size, end.size)
        return result
    }

    private fun mapTimeZone(calendar: Calendar, timeZoneFlags: Int): Byte {
        var offsetMillis = calendar.getTimeZone().rawOffset
        if (timeZoneFlags == TZ_FLAG_INCLUDE_DST_IN_TZ) {
            offsetMillis = calendar.getTimeZone().getOffset(calendar.getTimeInMillis())
        }
        val utcOffsetInQuarterHours = (offsetMillis / (1000 * 60 * 15))
        return utcOffsetInQuarterHours.toByte()
    }

    private fun calendarToRawBytes(timestamp: Calendar): ByteArray {
        val year = fromUint16(timestamp.get(Calendar.YEAR))
        return byteArrayOf(
            year[0],
            year[1],
            fromUint8(timestamp.get(Calendar.MONTH) + 1),
            fromUint8(timestamp.get(Calendar.DATE)),
            fromUint8(timestamp.get(Calendar.HOUR_OF_DAY)),
            fromUint8(timestamp.get(Calendar.MINUTE)),
            fromUint8(timestamp.get(Calendar.SECOND)),
            dayOfWeekToRawBytes(timestamp),
            0
        )
    }

    private fun dayOfWeekToRawBytes(calendar: Calendar): Byte {
        val calendarValue = calendar.get(Calendar.DAY_OF_WEEK)
        return if (calendarValue == Calendar.SUNDAY) {
            7
        } else {
            (calendarValue - 1).toByte()
        }
    }

    private fun shortCalendarToRawBytes(timestamp: Calendar): ByteArray {
        val year = fromUint16(timestamp.get(Calendar.YEAR))
        return byteArrayOf(
            year[0],
            year[1],
            fromUint8(timestamp.get(Calendar.MONTH) + 1),
            fromUint8(timestamp.get(Calendar.DATE)),
            fromUint8(timestamp.get(Calendar.HOUR_OF_DAY)),
            fromUint8(timestamp.get(Calendar.MINUTE)),
        )
    }

    private fun fromUint8(value: Int): Byte {
        return (value and 0xFF).toByte()
    }

    private fun fromUint16(value: Int): ByteArray {
        return byteArrayOf(
            (value and 0xFF).toByte(),
            ((value shr 8) and 0xFF).toByte()
        )
    }

    companion object {
        const val AUTH_BYTE: Byte = 0x08

        const val CRYPT_FLAGS: Byte = 0x00

        val UUID_CHARACTERISTIC_CHUNKED_TRANSFER_2021_READ =
            UUID.fromString("00000017-0000-3512-2118-0009af100700")

        val UUID_CHARACTERISTIC_CHUNKED_TRANSFER_2021_WRITE =
            UUID.fromString("00000016-0000-3512-2118-0009af100700")

        val UUID_CHARACTERISTIC_3_CONFIGURATION =
            UUID.fromString("00000003-0000-3512-2118-0009af100700")

        val UUID_CHARACTERISTIC_6_BATTERY_INFO =
            UUID.fromString("00000006-0000-3512-2118-0009af100700")

        val UUID_CHARACTERISTIC_AUDIO = UUID.fromString("00000012-0000-3512-2118-0009af100700")

        val UUID_CHARACTERISTIC_AUDIO_DATA = UUID.fromString("00000013-0000-3512-2118-0009af100700")

        val UUID_CHARACTERISTIC_DEVICE_EVENT =
            UUID.fromString("00000010-0000-3512-2118-0009af100700")

        val UUID_CHARACTERISTIC_WORKOUT = UUID.fromString("0000000f-0000-3512-2118-0009af100700")

        val UUID_CHARACTERISTIC_CURRENT_TIME = UUID.fromString((String.format("0000%s-0000-1000-8000-00805f9b34fb", "2A2B")))

        val UUID_CHARACTERISTIC_NOTIFICATION = UUID.fromString(String.format(BASE_UUID, "FF03"))

        val UUID_CHARACTERISTIC_AUTH = UUID.fromString("00000009-0000-3512-2118-0009af100700")

        /**/
        const val TZ_FLAG_INCLUDE_DST_IN_TZ = 1
    }
}