package by.bashlikovvv.bluetooth.model

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.os.ParcelUuid
import java.lang.reflect.InvocationTargetException
import java.util.UUID

data class GBDeviceCandidate(
    val device: BluetoothDevice,
    val rssi: Short? = null,
    private var _serviceUuids: Array<ParcelUuid>? = null,
) {
    val serviceUuids: Array<ParcelUuid>?
        get() = _serviceUuids

    var deviceName: String? = null
        private set

    val address: String
        get() = device.address

    val isBonded: Boolean
        @SuppressLint("MissingPermission")
        get() {
            return try {
                device.getBondState() == BluetoothDevice.BOND_BONDED
            } catch (_: SecurityException) {
                false
            }
        }

    @SuppressLint("MissingPermission")
    fun refreshName() {
        if (isNameKnown()) return

        try {
            val method = device::class.java.getMethod("getAliasName")
            deviceName = method.invoke(device) as String
        } catch (_: NoSuchMethodException) {
        } catch (_: IllegalAccessException) {
        } catch (_: InvocationTargetException) {}
        if (deviceName == null || deviceName?.isEmpty() == true) {
            try {
                deviceName = device.getName()
            } catch (_: SecurityException) {}
        }
    }

    fun supportsService(aService: UUID): Boolean {
        if (_serviceUuids == null || _serviceUuids?.isEmpty() == true) return false

        for (uuid in _serviceUuids) {
            if (aService == uuid.uuid) {
                return true
            }
        }

        return false
    }

    fun addUuids(newUuids: Array<ParcelUuid>) {
        _serviceUuids = mergeServiceUuids(serviceUuids ?: emptyArray(), newUuids)
    }

    fun isNameKnown(): Boolean = deviceName != null && deviceName?.isNotEmpty() == true

    private fun mergeServiceUuids(serviceUuids: Array<ParcelUuid>, deviceUuids: Array<ParcelUuid>): Array<ParcelUuid> {
        val uuids = LinkedHashSet<ParcelUuid>()
        uuids.addAll(serviceUuids)
        uuids.addAll(deviceUuids)
        return uuids.toTypedArray()
    }
}