package by.bashlikovvv.bluetooth.action

import android.annotation.SuppressLint
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import by.bashlikovvv.bluetooth.model.BtLEAction

class ReadAction(characteristic: BluetoothGattCharacteristic) : BtLEAction(characteristic) {
    @SuppressLint("MissingPermission")
    override fun run(gatt: BluetoothGatt): Boolean {
        val properties = characteristic?.properties
        if (((properties ?: 0) and BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
            return gatt.readCharacteristic(characteristic)
        }
        return false;
    }

    override fun expectsResult(): Boolean = true
}