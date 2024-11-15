package by.bashlikovvv.common.repository

import android.bluetooth.BluetoothDevice
import by.bashlikovvv.bluetooth.devices.huami.HuamiNotificationType
import by.bashlikovvv.bluetooth.devices.huami.HuamiSupport
import by.bashlikovvv.bluetooth.model.AbstractDeviceSupport
import by.bashlikovvv.bluetooth.model.DeviceType
import by.bashlikovvv.bluetooth.model.GBDevice
import by.bashlikovvv.bluetooth.model.QueueEntitiesProvider
import by.bashlikovvv.bluetooth.model.Reminder
import by.bashlikovvv.bluetooth.service.DeviceSupportFactory
import by.bashlikovvv.common.local.ConnectedDevicesLocalDataSource
import by.bashlikovvv.domain.base.AppDispatchers
import by.bashlikovvv.domain.model.BluetoothDeviceType
import by.bashlikovvv.domain.model.BluetoothService
import by.bashlikovvv.domain.model.NotificationTypes
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
        support = (DeviceSupportFactory.createDeviceSupport(
            device = GBDevice(
                device = device,
                deviceType = getDeviceType(device)
            ),
            queueEntitiesProvider = queueEntitiesProvider
        ) as? AbstractDeviceSupport)
            ?.also { deviceSupport ->
                if (deviceSupport is HuamiSupport) {
                    deviceSupport.setKey("0xf6747305a017528f08141d2844db4210")
                }
            }

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
            val device = queueEntitiesProvider.getAdapter()?.getRemoteDevice(deviceAddress)!!
            support = (DeviceSupportFactory.createDeviceSupport(
                device = GBDevice(
                    device = device,
                    deviceType = getDeviceType(device)
                ),
                queueEntitiesProvider = queueEntitiesProvider
            ) as? AbstractDeviceSupport)
                ?.also { deviceSupport ->
                    if (deviceSupport is HuamiSupport) {
                        deviceSupport.setKey("0xf6747305a017528f08141d2844db4210")
                    }
                }
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
        notificationType: NotificationTypes,
        test: Boolean,
        repeat: Short,
        onOffSequence: IntArray,
    ) = withContext(ioDispatcher) {
        val bluetoothNotificationType = when(notificationType) {
            is NotificationTypes.Alarm -> HuamiNotificationType.ALARM
            is NotificationTypes.AppAlerts -> HuamiNotificationType.APP_ALERTS
            is NotificationTypes.EventReminder -> HuamiNotificationType.EVENT_REMINDER
            is NotificationTypes.FindBand -> HuamiNotificationType.FIND_BAND
            is NotificationTypes.GoalNotification -> HuamiNotificationType.GOAL_NOTIFICATION
            is NotificationTypes.IdleAlerts -> HuamiNotificationType.IDLE_ALERTS
            is NotificationTypes.IncomingCall -> HuamiNotificationType.INCOMING_CALL
            is NotificationTypes.IncomingSms -> HuamiNotificationType.INCOMING_SMS
            is NotificationTypes.Schedule -> HuamiNotificationType.SCHEDULE
            is NotificationTypes.TodoList -> HuamiNotificationType.TODO_LIST
        }
        support?.setVibrationProfile(bluetoothNotificationType, test, repeat, onOffSequence)
    }

    private fun getDeviceType(device: BluetoothDevice): DeviceType {
        return when(bluetoothService.getDeviceType(device, null, null)) {
            BluetoothDeviceType.Unknown -> DeviceType.UNKNOWN
            BluetoothDeviceType.MiBand5 -> DeviceType.MI_BAND_5
        }
    }
}