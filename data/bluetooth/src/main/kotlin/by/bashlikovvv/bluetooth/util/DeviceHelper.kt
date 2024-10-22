package by.bashlikovvv.bluetooth.util

import by.bashlikovvv.bluetooth.model.DeviceType
import by.bashlikovvv.bluetooth.model.GBDeviceCandidate

object DeviceHelper {
    private val deviceTypeCache = HashMap<String, DeviceType>()

    private var orderedDeviceTypes: Array<DeviceType>? = null

    fun resolveDeviceType(deviceCandidate: GBDeviceCandidate): DeviceType {
        return resolveDeviceType(deviceCandidate, true)
    }

    fun resolveDeviceType(deviceCandidate: GBDeviceCandidate, useCache: Boolean): DeviceType {
        synchronized(this) {
            if (useCache) {
                val cachedType = deviceTypeCache[deviceCandidate.getMacAddress().lowercase()]
                if (cachedType != null) {
                    return cachedType
                }
            }

            for (type in getOrderedDeviceTypes()) {
                if (type.getDeviceCoordinator().supports(deviceCandidate)) {
                    deviceTypeCache[deviceCandidate.getMacAddress().lowercase()] = type
                    return type
                }
            }
            deviceTypeCache[deviceCandidate.getMacAddress().lowercase()] = DeviceType.UNKNOWN
        }
        return DeviceType.UNKNOWN
    }

    private fun getOrderedDeviceTypes(): Array<DeviceType> {
        if (orderedDeviceTypes == null) {
            val orderedDevices = DeviceType.values().toMutableList()
            orderedDevices.sortBy { it.getDeviceCoordinator().orderPriority }
            orderedDeviceTypes = orderedDevices.toTypedArray()
        }
        return orderedDeviceTypes ?: emptyArray()
    }
}