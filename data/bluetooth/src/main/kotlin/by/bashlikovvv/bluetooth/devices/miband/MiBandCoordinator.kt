package by.bashlikovvv.bluetooth.devices.miband

import android.annotation.SuppressLint
import android.bluetooth.BluetoothClass
import android.bluetooth.BluetoothDevice
import by.bashlikovvv.bluetooth.model.GBDeviceCandidate
import by.bashlikovvv.bluetooth.devices.huami.HuamiCoordinator
import java.util.UUID

abstract class MiBandCoordinator : HuamiCoordinator() {
    override fun supports(candidate: GBDeviceCandidate): Boolean {
        val macAddress = candidate.address.uppercase()
        if (macAddress.startsWith(MAC_ADDRESS_FILTER_1_1A)
            || macAddress.startsWith(MAC_ADDRESS_FILTER_1S)) {
            return true
        }
        if (candidate.supportsService(UUID_SERVICE_MI_BAND_SERVICE)
            && !candidate.supportsService(UUID_SERVICE_MI_BAND2_SERVICE)) {
            return true
        }
        try {
            val device = candidate.device
            if (isHealthWearable(device)) {
                candidate.refreshName()
                val name = candidate.deviceName
                if (name != null && name.uppercase().startsWith(MI_GENERAL_NAME_PREFIX)) {
                    return true
                }
            }
        } catch (_: Exception) {
        }

        return false
    }

    @SuppressLint("MissingPermission")
    fun isHealthWearable(device: BluetoothDevice): Boolean {
        var bluetoothClass: BluetoothClass? = null
        try {
            bluetoothClass = device.bluetoothClass
        } catch (_: SecurityException) {
            return false
        }
        if (bluetoothClass == null) {
            return false
        }
        if (bluetoothClass.majorDeviceClass == BluetoothClass.Device.Major.WEARABLE
            || bluetoothClass.majorDeviceClass == BluetoothClass.Device.Major.UNCATEGORIZED) {
            val deviceClasses = (BluetoothClass.Device.HEALTH_BLOOD_PRESSURE or
                    BluetoothClass.Device.HEALTH_DATA_DISPLAY or
                    BluetoothClass.Device.HEALTH_PULSE_RATE or
                    BluetoothClass.Device.HEALTH_WEIGHING or
                    BluetoothClass.Device.HEALTH_UNCATEGORIZED or
                    BluetoothClass.Device.HEALTH_PULSE_OXIMETER or
                    BluetoothClass.Device.HEALTH_GLUCOSE)
            return (bluetoothClass.deviceClass and deviceClasses) != 0
        }

        return false
    }

    companion object {
        const val MAC_ADDRESS_FILTER_1_1A = "88:0F:10"

        const val MAC_ADDRESS_FILTER_1S = "C8:0F:10"

        const val MI_GENERAL_NAME_PREFIX = "MI"

        val UUID_SERVICE_MI_BAND_SERVICE = UUID.fromString(String.format(BASE_UUID, "FEE0"))

        val UUID_SERVICE_MI_BAND2_SERVICE = UUID.fromString(String.format(BASE_UUID, "FEE1"))
    }
}