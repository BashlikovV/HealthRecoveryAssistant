package by.bashlikovvv.common.remote.bluetooth

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.ParcelUuid
import by.bashlikovvv.bluetooth.model.DeviceType
import by.bashlikovvv.bluetooth.model.GBDevice
import by.bashlikovvv.bluetooth.model.GBDeviceCandidate
import by.bashlikovvv.domain.base.BaseResult
import by.bashlikovvv.domain.base.SystemServiceProvider
import by.bashlikovvv.domain.model.BluetoothDeviceType
import by.bashlikovvv.domain.model.BluetoothService

class BluetoothServiceImpl(
    private val systemServiceProvider: SystemServiceProvider,
    private val checkSelfPermission: (String) -> Int,
) : BluetoothService {
    private var availableDevices = mutableMapOf<String, GBDevice>()

    override val bluetoothEnabled: Boolean = getBluetoothAdapter()?.isEnabled == true

    private var orderedDeviceTypes: Array<DeviceType>? = null

    override fun getBluetoothManager(): BluetoothManager? =
        systemServiceProvider.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager

    override fun getBluetoothAdapter(): BluetoothAdapter? = getBluetoothManager()?.adapter

    @SuppressLint("MissingPermission")
    override fun startDiscovery(): BaseResult<Unit> {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && checkSelfPermission(
                Manifest.permission.BLUETOOTH_SCAN
            ) == PackageManager.PERMISSION_DENIED
        ) return BaseResult.Failure(SecurityException())
        try {
            getBluetoothAdapter()?.startDiscovery()
                ?: return BaseResult.Failure(NullPointerException())
        } catch (e: SecurityException) {
            BaseResult.Failure(e)
        }

        return BaseResult.Success(Unit)
    }

    @SuppressLint("MissingPermission")
    override fun cancelDiscovery(): BaseResult<Unit> {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && checkSelfPermission(
                Manifest.permission.BLUETOOTH_SCAN
            ) == PackageManager.PERMISSION_DENIED
        ) return BaseResult.Failure(SecurityException())
        try {
            getBluetoothAdapter()?.cancelDiscovery() ?: return BaseResult.Failure(NullPointerException())
        } catch (e: SecurityException) {
            return BaseResult.Failure(e)
        }

        return BaseResult.Success(Unit)
    }

    @SuppressLint("MissingPermission")
    override fun getBoundDevices(): List<BluetoothDevice> {
        return try {
            getBluetoothAdapter()?.bondedDevices?.toList() ?: emptyList()
        } catch (_: SecurityException) {
            emptyList()
        }
    }

    /**
     * @return true when device type is supported
     * */
    override fun addBluetoothDevice(
        device: BluetoothDevice,
        rssi: Short?,
        uuids: Array<ParcelUuid>?,
    ): Boolean {
        val gbDevice = getGbDevice(device, rssi, uuids)
        return if (gbDevice.deviceType != DeviceType.UNKNOWN) {
            availableDevices.put(device.address, gbDevice)
            true
        } else {
            true
        }
    }

    override fun getBluetoothDeviceByAddress(address: String): BluetoothDevice? {
        return availableDevices.getOrElse(address) { null }?.device
    }

    override fun getDeviceTypeByAddress(address: String): BluetoothDeviceType? {
        return when(availableDevices.getOrElse(address) { null }?.deviceType) {
            DeviceType.UNKNOWN -> null
            DeviceType.MI_BAND_5 -> BluetoothDeviceType.MiBand5
            null -> null
        }
    }

    @SuppressLint("MissingPermission")
    override fun bondDevice(address: String): BaseResult<Boolean> {
        return try {
            BaseResult.Success(getBluetoothDeviceByAddress(address)?.createBond() == true)
        } catch (e: SecurityException) {
            BaseResult.Failure(e)
        }
    }

    override fun getDevices(): List<BluetoothDevice> {
        return availableDevices.values.map { it.device }
    }

    override fun getDeviceType(
        device: BluetoothDevice,
        rssi: Short?,
        uuids: Array<ParcelUuid>?,
    ): BluetoothDeviceType {
        return when(resolveType(device, rssi, uuids)) {
            DeviceType.UNKNOWN -> BluetoothDeviceType.Unknown
            DeviceType.MI_BAND_5 -> BluetoothDeviceType.MiBand5
        }
    }

    fun getGbDevice(
        device: BluetoothDevice,
        rssi: Short?,
        uuids: Array<ParcelUuid>?,
    ): GBDevice {
        return GBDevice(
            device = device,
            deviceType = resolveType(device, rssi, uuids),
        )
    }

    private fun resolveType(
        device: BluetoothDevice,
        rssi: Short?,
        uuids: Array<ParcelUuid>?,
    ): DeviceType {
        for (type in getDeviceTypes()) {
            if (type.getDeviceCoordinator().supports(GBDeviceCandidate(device, rssi, uuids))) {
                return type
            }
        }

        return DeviceType.UNKNOWN
    }

    private fun getDeviceTypes(): Array<DeviceType> {
        synchronized(this) {
            if (orderedDeviceTypes == null) {
                orderedDeviceTypes = DeviceType.entries
                    .sortedBy { it.getDeviceCoordinator().orderPriority }
                    .toTypedArray()
            }
            return orderedDeviceTypes!!
        }
    }
}