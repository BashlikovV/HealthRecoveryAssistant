package by.bashlikovvv.common.repository

import android.bluetooth.BluetoothDevice
import by.bashlikovvv.bluetooth.model.GBDevice
import by.bashlikovvv.bluetooth.model.QueueEntitiesProvider
import by.bashlikovvv.bluetooth.model.Reminder
import by.bashlikovvv.bluetooth.model.Reminder.Builder.Companion.MAX_REMINDER_MESSAGE_LENGTH
import by.bashlikovvv.bluetooth.service.BtLEQueue
import by.bashlikovvv.bluetooth.transactioin.TransactionBuilder
import by.bashlikovvv.domain.model.ReminderDescription
import java.nio.charset.StandardCharsets
import java.util.UUID

class BluetoothRepository(
    private val queueEntitiesProvider: QueueEntitiesProvider,
) {
    private var queue: BtLEQueue? = null

    fun connect(device: BluetoothDevice): Boolean {
        val tmpQueue = BtLEQueue(
            device = GBDevice(
                device = device,
            ),
            queueEntitiesProvider = queueEntitiesProvider,
        )
        queue = tmpQueue

        return tmpQueue.connect()
    }

    fun disconnect() {
        queue?.disconnect()
    }

    fun sendFindDeviceCommand(start: Boolean) {
        queue?.let { queueNotNull ->
            val characteristics = queueNotNull.getCharacteristic(UUID_CHARACTERISTIC_ALERT_LEVEL)
            val tb = TransactionBuilder("find device")
            tb.write(characteristics, if (start) byteArrayOf(3) else byteArrayOf(0))
            tb.queue(queueNotNull)
        }
    }

    fun sendCreateReminderCommand(reminder: ReminderDescription) {
        val reminderMessage = Reminder.truncate(reminder.message, MAX_REMINDER_MESSAGE_LENGTH)
            .toByteArray(StandardCharsets.UTF_8)
        val eventConfig = 0x01 or 0x08
        val reminderRepresentation = Reminder.Builder(reminderMessage.size)
            .put(0x0B)
            .position(1)
            .putInt(eventConfig)
            .date(reminder.date)
            .put(0x00)
            .message(reminderMessage)
            .put(0x00)
            .build()
        queue?.let { queueNotNull ->
            val characteristics = queueNotNull.getCharacteristic(UUID_CHARACTERISTIC_CHUNKED_TRANSFER)
            val tb = TransactionBuilder("find device")
            tb.writeToChunkedOld(characteristics, 2, reminderRepresentation)
            tb.queue(queueNotNull)
        }
    }

    companion object {
        const val BASE_UUID = "0000%s-0000-1000-8000-00805f9b34fb"
        val UUID_CHARACTERISTIC_ALERT_LEVEL = UUID.fromString(String.format(BASE_UUID, "2A06"))
        val UUID_CHARACTERISTIC_CHUNKED_TRANSFER = UUID.fromString("00000020-0000-3512-2118-0009af100700");
    }
}