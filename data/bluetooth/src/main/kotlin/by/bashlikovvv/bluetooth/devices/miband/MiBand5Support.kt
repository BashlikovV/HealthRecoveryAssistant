package by.bashlikovvv.bluetooth.devices.miband

import by.bashlikovvv.bluetooth.devices.huami.HuamiSupport
import by.bashlikovvv.bluetooth.model.GBDevice
import by.bashlikovvv.bluetooth.model.QueueEntitiesProvider
import by.bashlikovvv.bluetooth.model.Reminder
import by.bashlikovvv.bluetooth.model.Reminder.Builder.Companion.MAX_REMINDER_MESSAGE_LENGTH
import by.bashlikovvv.bluetooth.transactioin.TransactionBuilder
import java.nio.charset.StandardCharsets
import java.util.Date
import java.util.UUID

class MiBand5Support(
    device: GBDevice,
    provider: QueueEntitiesProvider,
) : HuamiSupport(device, provider) {
    override fun onFindDevice(start: Boolean) {
        mQueue?.let { queueNotNull ->
            val characteristics = queueNotNull.getCharacteristic(UUID_CHARACTERISTIC_ALERT_LEVEL)
            val tb = TransactionBuilder("find device")
            tb.write(characteristics, if (start) byteArrayOf(3) else byteArrayOf(0))
            tb.queue(queueNotNull)
        }
    }

    override fun setReminders(reminders: List<Reminder>) {
        reminders.forEach { sendCreateReminderCommand(it.message, it.date) }
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
            val tb = TransactionBuilder("set reminder")
            tb.writeToChunkedOld(characteristics, 2, reminderRepresentation)
            tb.queue(queueNotNull)
        }
    }

    companion object {
        const val BASE_UUID = "0000%s-0000-1000-8000-00805f9b34fb"
        val UUID_CHARACTERISTIC_ALERT_LEVEL = UUID.fromString(String.format(BASE_UUID, "2A06"))
        val UUID_CHARACTERISTIC_CHUNKED_TRANSFER = UUID.fromString("00000020-0000-3512-2118-0009af100700")
    }
}