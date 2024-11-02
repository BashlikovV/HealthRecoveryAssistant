package by.bashlikovvv.bluetooth.action

import android.annotation.SuppressLint
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothStatusCodes
import android.os.Build
import by.bashlikovvv.bluetooth.model.BtLEAction
import java.util.UUID

class NotifyAction : BtLEAction {
    private val enableFlag: Boolean

    private var hasWrittenDescriptor: Boolean = false

    constructor(
        characteristic: BluetoothGattCharacteristic?,
        enable: Boolean
    ) : super(characteristic) {
        this.enableFlag = enable
    }

    @SuppressLint("MissingPermission")
    override fun run(gatt: BluetoothGatt): Boolean {
        var result = gatt.setCharacteristicNotification(characteristic, enableFlag)
        if (result) {
            val clientCharConfigDescriptor = characteristic?.getDescriptor(
                UUID_DESCRIPTOR_GATT_CLIENT_CHARACTERISTIC_CONFIGURATION
            )

            if (clientCharConfigDescriptor != null) {
                val properties = characteristic.properties
                when {
                    (properties and BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0 -> {
                        val value = if (enableFlag)
                            BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                        else
                            BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE
                        result = writeDescriptor(gatt, clientCharConfigDescriptor, value)
                        hasWrittenDescriptor = true
                    }
                    (properties and BluetoothGattCharacteristic.PROPERTY_INDICATE) > 0 -> {
                        val value = if (enableFlag)
                            BluetoothGattDescriptor.ENABLE_INDICATION_VALUE
                        else
                            BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE
                        result = writeDescriptor(gatt, clientCharConfigDescriptor, value)
                        hasWrittenDescriptor = true
                    }
                    else -> { hasWrittenDescriptor = false }
                }
            } else {
                hasWrittenDescriptor = false
            }
        } else {
            hasWrittenDescriptor = false
        }

        return result
    }

    override fun expectsResult(): Boolean = hasWrittenDescriptor

    @SuppressLint("MissingPermission")
    private fun writeDescriptor(
        gatt: BluetoothGatt,
        descriptor: BluetoothGattDescriptor,
        value: ByteArray
    ): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            try {
                val result = gatt.writeDescriptor(descriptor, value)
                if (result != BluetoothStatusCodes.SUCCESS) return false
            } catch (_: SecurityException) {
                return false
            }
        } else {
            if (!descriptor.setValue(value)) return false
            return gatt.writeDescriptor(descriptor)
        }

        return true
    }

    companion object {
        const val BASE_UUID = "0000%s-0000-1000-8000-00805f9b34fb"

        val UUID_DESCRIPTOR_GATT_CLIENT_CHARACTERISTIC_CONFIGURATION =
            UUID.fromString((String.format(BASE_UUID, "2902")))
    }
}