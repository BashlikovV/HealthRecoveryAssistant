package by.bashlikovvv.bluetooth.devices.miband

import android.util.Log
import by.bashlikovvv.bluetooth.devices.huami.HuamiNotificationType
import by.bashlikovvv.bluetooth.devices.huami.HuamiSupport
import by.bashlikovvv.bluetooth.model.GBDevice
import by.bashlikovvv.bluetooth.model.QueueEntitiesProvider
import by.bashlikovvv.bluetooth.model.Reminder
import by.bashlikovvv.bluetooth.model.Reminder.Builder.Companion.MAX_REMINDER_MESSAGE_LENGTH
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.StandardCharsets
import java.util.Date
import java.util.UUID
import kotlin.experimental.or

class MiBand5Support(
    key: String,
    device: GBDevice,
    provider: QueueEntitiesProvider,
) : HuamiSupport(key ,device, provider) {
    override fun connect(): Boolean {
        repeat(5) {
            if (super.connect()) {
                return true
            }
        }

        return false
    }

    override fun onFindDevice(start: Boolean) {
        mQueue?.let { queueNotNull ->
            val characteristics = queueNotNull.getCharacteristic(UUID_CHARACTERISTIC_ALERT_LEVEL)
            val tb = performInitialized("find device")
            tb.write(characteristics, if (start) byteArrayOf(3) else byteArrayOf(0))
            tb.queue(queueNotNull)
        }
    }

    override fun setReminders(reminders: List<Reminder>) {
        reminders.forEach { sendCreateReminderCommand(it.message, it.date) }
    }

//    test(HuamiNotificationType.FIND_BAND, true, (1).toShort(), intArrayOf(30, 35, 30, 35, 30, 35, 30, 800))
    @Suppress("UNUSED")
    private fun test(
        notificationType: HuamiNotificationType,
        test: Boolean,
        repeat: Short,
        onOffSequence: IntArray
    ): ByteArray {
        val maxTotalLength = 10_000
        val onOff = truncateVibrationsOnOff(
            repeat = repeat,
            onOffSequence = onOffSequence,
            limitMillis = maxTotalLength
        )
        val buf = ByteBuffer.allocate(3 + 2 * onOff.size)
        buf.order(ByteOrder.LITTLE_ENDIAN)

        buf.put((0x20).toByte())
        buf.put(notificationType.code)
        var flag = (onOff.size / 2).toByte()
        flag = flag or (0x40).toByte()
        if (test) {
            flag = flag or (0x80).toByte()
        }
        buf.put(flag)
        for (time in onOff) {
            buf.putShort(time)
        }

        mQueue?.let { queueNotNull ->
            val characteristic = getCharacteristic(UUID_CHARACTERISTIC_CHUNKED_TRANSFER)
            val tb = performInitialized("set vibration profile")
            tb.writeToChunkedOld(characteristic, 2, buf.array())
            tb.queue(queueNotNull)
        }

        return buf.array()
    }

    override fun setVibrationProfile(
        notificationType: HuamiNotificationType,
        test: Boolean,
        repeat: Short,
        onOffSequence: IntArray
    ) {
        val maxTotalLength = 10_000
        val onOff = truncateVibrationsOnOff(
            repeat = repeat,
            onOffSequence = onOffSequence,
            limitMillis = maxTotalLength
        )
        val buf = ByteBuffer.allocate(3 + 2 * onOff.size)
        buf.order(ByteOrder.LITTLE_ENDIAN)

        buf.put((0x20).toByte())
        buf.put(notificationType.code)
        var flag = (onOff.size / 2).toByte()
        flag = flag or (0x40).toByte()
        if (test) {
            flag = flag or (0x80).toByte()
        }
        buf.put(flag)
        for (time in onOff) {
            buf.putShort(time)
        }

        mQueue?.let { queueNotNull ->
            val characteristic = getCharacteristic(UUID_CHARACTERISTIC_CHUNKED_TRANSFER)
            val tb = performInitialized("Sending configuration for option")
            tb.writeToChunkedOld(characteristic, 2, buf.array())
            tb.queue(queueNotNull)
        }
    }

    fun sendCreateReminderCommand(
        message: String,
        date: Date
    ) {
        val reminderMessage = Reminder.truncate(message, MAX_REMINDER_MESSAGE_LENGTH)
            .toByteArray(StandardCharsets.UTF_8)
        val eventConfig = 0x01 or 0x08
        val reminderRepresentation = Reminder.Builder(reminderMessage.size)
            .put(0x0B)
            .position(1)
            .putInt(eventConfig)
            .date(date)
            .put(0x00)
            .message(reminderMessage)
            .put(0x00)
            .build()
        mQueue?.let { queueNotNull ->
            val characteristics = queueNotNull.getCharacteristic(UUID_CHARACTERISTIC_CHUNKED_TRANSFER)
            val tb = performInitialized("set reminder")
            tb.writeToChunkedOld(characteristics, 2, reminderRepresentation)
            tb.queue(queueNotNull)
        }
    }

    override fun handle2021Payload(type: Short, payload: ByteArray) {
        Log.i("MYTAG", "type: $type, payload: $payload")
    }

    override fun getCryptFlags(): Byte = (0x80).toByte()

    companion object {
        const val BASE_UUID = "0000%s-0000-1000-8000-00805f9b34fb"
        val UUID_CHARACTERISTIC_ALERT_LEVEL = UUID.fromString(String.format(BASE_UUID, "2A06"))
        val UUID_CHARACTERISTIC_CHUNKED_TRANSFER = UUID.fromString("00000020-0000-3512-2118-0009af100700")
    }
}