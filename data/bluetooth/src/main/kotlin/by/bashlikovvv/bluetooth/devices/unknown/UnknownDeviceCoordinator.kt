package by.bashlikovvv.bluetooth.devices.unknown

import by.bashlikovvv.bluetooth.model.AbstractDeviceCoordinator
import by.bashlikovvv.bluetooth.model.DeviceSupport
import by.bashlikovvv.bluetooth.model.GBDevice
import by.bashlikovvv.bluetooth.model.Reminder
import kotlin.reflect.KClass

class UnknownDeviceCoordinator : AbstractDeviceCoordinator() {
    override fun getAlarmSlotCount(device: GBDevice): Int = 0

    override fun getAlarmTitleLimit(device: GBDevice): Int = 0

    override fun getBondingStyle(): Int = 0
    override fun getDeviceSupportClass(): KClass<out DeviceSupport> = UnknownDeviceSupport::class

    class UnknownDeviceSupport : DeviceSupport {
        override fun connect(): Boolean = false

        override fun setReminders(reminders: List<Reminder>) {}

        override fun onFindDevice(start: Boolean) {}
    }
}