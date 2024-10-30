package by.bashlikovvv.bluetooth.model

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothManager

interface QueueEntitiesProvider {
    fun getBluetoothManager(): BluetoothManager?

    fun getAdapter(): BluetoothAdapter?

    fun connectGatt(
        device: BluetoothDevice,
        callback: BluetoothGattCallback,
    ): BluetoothGatt
}