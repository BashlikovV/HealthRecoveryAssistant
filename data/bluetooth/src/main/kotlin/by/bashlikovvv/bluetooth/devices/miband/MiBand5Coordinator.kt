package by.bashlikovvv.bluetooth.devices.miband

import by.bashlikovvv.bluetooth.model.DeviceCoordinator
import by.bashlikovvv.bluetooth.model.DeviceSupport
import by.bashlikovvv.bluetooth.model.GBDevice
import kotlin.reflect.KClass

class MiBand5Coordinator : MiBandCoordinator() {
    override fun getAlarmTitleLimit(device: GBDevice): Int = ALARM_TITLE_LIMIT

    override fun getBondingStyle(): Int = DeviceCoordinator.Companion.BONDING_STYLE_REQUIRE_KEY

    override fun getReminderSlotCount(device: GBDevice): Int = REMINDER_SLOT_COUNT

    override fun getDeviceSupportClass(): KClass<out DeviceSupport> = MiBand5Support::class

    companion object {
        const val ALARM_TITLE_LIMIT = -1

        const val REMINDER_SLOT_COUNT = 50
    }
}