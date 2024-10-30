package by.bashlikovvv.bluetooth.service

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothManager
import android.content.Context
import by.bashlikovvv.bluetooth.model.QueueEntitiesProvider

class QueueEntitiesProviderImpl(
    private val context: Context
) : QueueEntitiesProvider {
    override fun getBluetoothManager(): BluetoothManager? {
        return context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
    }

    override fun getAdapter(): BluetoothAdapter? = getBluetoothManager()?.adapter

    @SuppressLint("MissingPermission")
    override fun connectGatt(
        device: BluetoothDevice,
        callback: BluetoothGattCallback
    ): BluetoothGatt = device.connectGatt(context, false, callback, BluetoothDevice.TRANSPORT_LE)
}