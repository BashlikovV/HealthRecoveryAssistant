package by.bashlikovvv.domain.model

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import by.bashlikovvv.domain.base.BaseResult

interface BluetoothService {
    val bluetoothEnabled: Boolean

    fun getBluetoothManager(): BluetoothManager?

    fun getBluetoothAdapter(): BluetoothAdapter?

    fun startDiscovery(): BaseResult<Unit>

    fun cancelDiscovery(): BaseResult<Unit>

    fun getBoundDevices(): List<BluetoothDevice>

    fun addBluetoothDevice(device: BluetoothDevice)

    fun getBluetoothDeviceByAddress(address: String): BluetoothDevice?

    fun bondDevice(address: String): BaseResult<Boolean>

    fun getDevices(): List<BluetoothDevice>
}