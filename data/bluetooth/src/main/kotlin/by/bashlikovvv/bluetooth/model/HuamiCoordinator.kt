package by.bashlikovvv.bluetooth.model

import android.bluetooth.le.ScanFilter
import android.os.ParcelUuid
import java.util.Collections
import java.util.UUID

abstract class HuamiCoordinator : AbstractDeviceCoordinator() {
    override val orderPriority: Int = 1

    override fun createBLEScanFilters(): List<ScanFilter> {
        val mi2Service = ParcelUuid(UUID_SERVICE_MI_BAND2_SERVICE)
        val filter = ScanFilter.Builder()
            .setServiceUuid(mi2Service)
            .build()
        return Collections.singletonList(filter)
    }

    override fun getAlarmSlotCount(device: GBDevice): Int = ALARM_SLOT_COUNT

    open fun getMaximumReminderMessageLength(): Int = MAXIMUM_REMINDER_MESSAGE_LENGTH

    open fun getReminderSlotCount(device: GBDevice): Int = REMINDER_SLOT_COUNT

    companion object {
        val UUID_SERVICE_MI_BAND2_SERVICE = UUID.fromString(String.format(BASE_UUID, "FEE1"))

        const val ALARM_SLOT_COUNT = 10

        const val MAXIMUM_REMINDER_MESSAGE_LENGTH = 16

        const val REMINDER_SLOT_COUNT = 22
    }
}