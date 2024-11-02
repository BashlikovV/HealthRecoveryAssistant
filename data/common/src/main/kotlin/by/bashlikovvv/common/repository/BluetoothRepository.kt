package by.bashlikovvv.common.repository

import android.bluetooth.BluetoothDevice
import by.bashlikovvv.bluetooth.devices.miband.MiBand5Support
import by.bashlikovvv.bluetooth.model.AbstractDeviceSupport
import by.bashlikovvv.bluetooth.model.DeviceType
import by.bashlikovvv.bluetooth.model.GBDevice
import by.bashlikovvv.bluetooth.model.QueueEntitiesProvider
import by.bashlikovvv.bluetooth.model.Reminder
import by.bashlikovvv.domain.base.AppDispatchers
import by.bashlikovvv.domain.model.ReminderDescription
import kotlinx.coroutines.withContext

class BluetoothRepository(
    private val queueEntitiesProvider: QueueEntitiesProvider,
    appDispatchers: AppDispatchers,
) {
    private val ioDispatcher = appDispatchers.io

    private var support: AbstractDeviceSupport? = null

    suspend fun connect(device: BluetoothDevice): Boolean = withContext(ioDispatcher) {
        support = MiBand5Support(
            "0xe54a0bde189cd6a78fcf408d9bdb5fa5",
            GBDevice(device, DeviceType.MI_BAND_5),
            queueEntitiesProvider
        )

        support?.connect() == true
    }

    suspend fun sendFindDeviceCommand(start: Boolean) = withContext(ioDispatcher) {
        support?.onFindDevice(start)
    }

    suspend fun sendCreateReminderCommand(reminder: ReminderDescription) = withContext(ioDispatcher) {
        support?.setReminders(
            listOf(Reminder(reminder.message, reminder.date))
        )
    }
}