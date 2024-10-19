@file:SuppressLint("MissingPermission")

package by.bashlikovvv.bluetooth.actions

import android.annotation.SuppressLint
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import by.bashlikovvv.bluetooth.model.BtLEAction

/**
 * Invokes a read operation on a given GATT characteristic.
 * The result will be made available asynchronously through the
 * {@link BluetoothGattCallback}
 */
class ReadAction(characteristic: BluetoothGattCharacteristic) : BtLEAction(characteristic) {
    override fun run(gatt: BluetoothGatt): Boolean {
        val properties = characteristic?.properties ?: 0
        if ((properties and BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
            return gatt.readCharacteristic(characteristic)
        }
        return false
    }

    override fun expectsResult(): Boolean = true
}