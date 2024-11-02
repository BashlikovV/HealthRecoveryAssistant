package by.bashlikovvv.domain.source

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import by.bashlikovvv.domain.base.BaseResult
import by.bashlikovvv.domain.base.SystemServiceProvider
import by.bashlikovvv.domain.model.BluetoothService

class BluetoothServiceImpl(
    private val systemServiceProvider: SystemServiceProvider,
    private val checkSelfPermission: (String) -> Int,
) : BluetoothService {
    private var availableDevices = mutableMapOf<String, BluetoothDevice>()

    override val bluetoothEnabled: Boolean = getBluetoothAdapter()?.isEnabled == true

    override fun getBluetoothManager(): BluetoothManager? =
        systemServiceProvider.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager

    override fun getBluetoothAdapter(): BluetoothAdapter? = getBluetoothManager()?.adapter

    @SuppressLint("MissingPermission")
    override fun startDiscovery(): BaseResult<Unit> {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && checkSelfPermission(
                android.Manifest.permission.BLUETOOTH_SCAN
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
                android.Manifest.permission.BLUETOOTH_SCAN
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

    override fun addBluetoothDevice(device: BluetoothDevice) {
        availableDevices.put(device.address, device)
    }

    override fun getBluetoothDeviceByAddress(address: String): BluetoothDevice? {
        return availableDevices.getOrElse(address) { null }
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
        return availableDevices.values.toList()
    }
}