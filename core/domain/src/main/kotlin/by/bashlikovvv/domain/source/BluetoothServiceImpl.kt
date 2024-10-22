package by.bashlikovvv.domain.source

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import by.bashlikovvv.domain.base.BaseResult
import by.bashlikovvv.domain.base.SystemServiceProvider
import by.bashlikovvv.domain.model.BluetoothService

class BluetoothServiceImpl(
    private val systemServiceProvider: SystemServiceProvider,
    private val checkSelfPermission: (String) -> Int
) : BluetoothService {
    override fun getBluetoothManager(): BluetoothManager? =
        systemServiceProvider.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager

    override fun getBluetoothAdapter(): BluetoothAdapter? = getBluetoothManager()?.adapter

    @SuppressLint("MissingPermission")
    override fun startDiscovery(): BaseResult<Unit> {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && checkSelfPermission(
                android.Manifest.permission.BLUETOOTH_SCAN
            ) == PackageManager.PERMISSION_DENIED
        ) return BaseResult.Failure(SecurityException())
        getBluetoothAdapter()?.startDiscovery()
            ?: return BaseResult.Failure(NullPointerException())

        return BaseResult.Success(Unit)
    }

    @SuppressLint("MissingPermission")
    override fun cancelDiscovery(): BaseResult<Unit> {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && checkSelfPermission(
                android.Manifest.permission.BLUETOOTH_SCAN
            ) == PackageManager.PERMISSION_DENIED
        ) return BaseResult.Failure(SecurityException())
        getBluetoothAdapter()?.cancelDiscovery() ?: return BaseResult.Failure(NullPointerException())

        return BaseResult.Success(Unit)
    }

    @SuppressLint("MissingPermission")
    override fun getBoundDevices(): List<BluetoothDevice> {
        return getBluetoothAdapter()?.bondedDevices?.toList() ?: emptyList()
    }
}