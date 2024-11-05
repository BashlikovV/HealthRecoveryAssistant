package by.bashlikovvv.common.repository

import android.bluetooth.BluetoothDevice
import by.bashlikovvv.bluetooth.devices.huami.HuamiNotificationType
import by.bashlikovvv.bluetooth.devices.miband.MiBand5Support
import by.bashlikovvv.bluetooth.model.AbstractDeviceSupport
import by.bashlikovvv.bluetooth.model.DeviceType
import by.bashlikovvv.bluetooth.model.GBDevice
import by.bashlikovvv.bluetooth.model.QueueEntitiesProvider
import by.bashlikovvv.bluetooth.model.Reminder
import by.bashlikovvv.common.local.ConnectedDevicesLocalDataSource
import by.bashlikovvv.domain.base.AppDispatchers
import by.bashlikovvv.domain.model.BluetoothService
import by.bashlikovvv.domain.model.ReminderDescription
import kotlinx.coroutines.withContext

class BluetoothRepository(
    private val bluetoothService: BluetoothService,
    private val queueEntitiesProvider: QueueEntitiesProvider,
    private val connectedDevicesLocalDataSource: ConnectedDevicesLocalDataSource,
    appDispatchers: AppDispatchers,
) {
    private val ioDispatcher = appDispatchers.io

    private var support: AbstractDeviceSupport? = null

    val connectedDevices = connectedDevicesLocalDataSource.getConnectedDevices()

    suspend fun connectFirstTime(device: BluetoothDevice): Boolean = withContext(ioDispatcher) {
        support = MiBand5Support(
            "0x2752cc9ca28106d7d9b128119c50c9f9",
            GBDevice(device, DeviceType.MI_BAND_5),
            queueEntitiesProvider
        )

        val result = support?.connect() == true
        if (result) {
            by.bashlikovvv.domain.model.BluetoothDevice.fromAndroidBluetoothDevice(
                device = device,
                type = bluetoothService.getDeviceTypeByAddress(device.address)!!,
            )?.let { domainDevice ->
                connectedDevicesLocalDataSource.addConnectedDevice(domainDevice)
            }
        }

        result
    }

    suspend fun connect(deviceAddress: String): Boolean = withContext(ioDispatcher) {
        try {
            support = MiBand5Support(
                "0x2752cc9ca28106d7d9b128119c50c9f9",
                GBDevice(queueEntitiesProvider.getAdapter()?.getRemoteDevice(deviceAddress)!!, DeviceType.MI_BAND_5),
                queueEntitiesProvider
            )
        } catch (_: Exception) {
            return@withContext false
        }

        support?.connect() == true
    }

    fun isConnected(deviceAddress: String): Boolean {
        return support?.device?.device?.address == deviceAddress
    }

    suspend fun sendFindDeviceCommand(start: Boolean) = withContext(ioDispatcher) {
        support?.onFindDevice(start)
    }

    suspend fun sendCreateReminderCommand(reminder: ReminderDescription) = withContext(ioDispatcher) {
        support?.setReminders(
            listOf(Reminder(reminder.message, reminder.date))
        )
    }

    suspend fun setVibrationProfile(
        test: Boolean,
        repeat: Short,
        onOffSequence: IntArray,
    ) = withContext(ioDispatcher) {
        support?.setVibrationProfile(
            HuamiNotificationType.FIND_BAND, test, repeat, onOffSequence,
        )
    }
}