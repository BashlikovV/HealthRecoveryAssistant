package by.bashlikovvv.common.repository

import android.bluetooth.BluetoothDevice
import by.bashlikovvv.bluetooth.devices.miband.MiBand5Support
import by.bashlikovvv.bluetooth.model.AbstractDeviceSupport
import by.bashlikovvv.bluetooth.model.DeviceType
import by.bashlikovvv.bluetooth.model.GBDevice
import by.bashlikovvv.bluetooth.model.QueueEntitiesProvider
import by.bashlikovvv.bluetooth.model.Reminder
import by.bashlikovvv.domain.model.ReminderDescription

class BluetoothRepository(
    private val queueEntitiesProvider: QueueEntitiesProvider,
) {
    private var support: AbstractDeviceSupport? = null

    fun connect(device: BluetoothDevice): Boolean {
        support = MiBand5Support(
            GBDevice(device, DeviceType.MI_BAND_5),
            queueEntitiesProvider
        )

        return support?.connect() == true
    }

    fun sendFindDeviceCommand(start: Boolean) {
        support?.onFindDevice(start)
    }

    fun sendCreateReminderCommand(reminder: ReminderDescription) {
        support?.setReminders(
            listOf(Reminder(reminder.message, reminder.date))
        )
    }
}