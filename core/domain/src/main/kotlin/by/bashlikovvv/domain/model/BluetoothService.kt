package by.bashlikovvv.domain.model

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.os.ParcelUuid
import by.bashlikovvv.domain.base.BaseResult

interface BluetoothService {
    val bluetoothEnabled: Boolean

    fun getBluetoothManager(): BluetoothManager?

    fun getBluetoothAdapter(): BluetoothAdapter?

    fun startDiscovery(): BaseResult<Unit>

    fun cancelDiscovery(): BaseResult<Unit>

    fun getBoundDevices(): List<BluetoothDevice>

    fun addBluetoothDevice(
        device: BluetoothDevice,
        rssi: Short? = null,
        uuids: Array<ParcelUuid>? = null,
    ): Boolean

    fun getBluetoothDeviceByAddress(address: String): BluetoothDevice?

    fun getDeviceTypeByAddress(address: String): BluetoothDeviceType?

    fun bondDevice(address: String): BaseResult<Boolean>

    fun getDevices(): List<BluetoothDevice>
}