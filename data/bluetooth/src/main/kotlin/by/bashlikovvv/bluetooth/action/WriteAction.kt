package by.bashlikovvv.bluetooth.action

import android.annotation.SuppressLint
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import by.bashlikovvv.bluetooth.model.BtLEAction

class WriteAction : BtLEAction {
    private val value: ByteArray

    constructor(
        characteristic: BluetoothGattCharacteristic,
        value: ByteArray
    ) : super(characteristic) {
        this.value = value
    }

    override fun run(gatt: BluetoothGatt): Boolean {
        val properties = characteristic?.properties
        if (((properties ?: 0) and BluetoothGattCharacteristic.PROPERTY_WRITE) > 0 || (((properties ?: 0) and BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) > 0)) {
            return writeValue(gatt, characteristic!!, value)
        }
        return false
    }

    @SuppressLint("MissingPermission")
    private fun writeValue(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, value: ByteArray): Boolean {
        if (characteristic.setValue(value)) {
            return gatt.writeCharacteristic(characteristic)
        }

        return false
    }

    override fun expectsResult(): Boolean = true
}