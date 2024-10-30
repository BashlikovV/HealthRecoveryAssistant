package by.bashlikovvv.bluetooth.model

import android.bluetooth.le.ScanFilter
import kotlin.reflect.KClass

interface DeviceCoordinator {
    /**
     * Returns the type of connection, Classic or BLE
     */
    val connectionType: ConnectionType

    /**
     * Returns false is the Device is not connectable,
     * only scannable, like beacons
     *
     * @return boolean
     */
    fun isConnectable(): Boolean

    /**
     * Checks whether this coordinator handles the given candidate.
     *
     * @param candidate
     * @return true if this coordinator handles the given candidate.
     */
    fun supports(candidate: GBDeviceCandidate): Boolean

    /**
     * Returns a list of scan filters that shall be used to discover devices supported
     * by this coordinator.
     * @return the list of scan filters, may be empty
     */
    fun createBLEScanFilters(): List<ScanFilter>

    fun createDevice(candidate: GBDeviceCandidate, deviceType: DeviceType): GBDevice

    /**
     * Returns the number of alarms this device/coordinator supports
     * Shall return 0 also if it is not possible to set alarms via
     * protocol, but only on the smart device itself.
     *
     * @return
     */
    fun getAlarmSlotCount(device: GBDevice): Int

    /**
     * Returns the character limit for the alarm title, negative if no limit.
     * @return
     */
    fun getAlarmTitleLimit(device: GBDevice): Int

    /**
     * Returns how/if the given device should be bonded before connecting to it.
     */
    fun getBondingStyle(): Int

    fun getDeviceSupportClass(): KClass<out DeviceSupport>

    enum class ConnectionType(
        val useBluetoothClassic: Boolean,
        val useBluetoothLE: Boolean,
    ) {
        BLE(false, true),
        BT_CLASSIC(true, false),
        BOTH(true, true);
    }

    companion object {
        const val BONDING_STYLE_NONE = 0

        const val BONDING_STYLE_BOND = 1

        const val BONDING_STYLE_ASK = 2

        const val BONDING_STYLE_REQUIRE_KEY = 3

        const val BONDING_STYLE_LAZY = 4
    }
}