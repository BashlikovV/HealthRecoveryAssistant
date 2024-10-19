package by.bashlikovvv.bluetooth.model

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor

/**
 * Callback interface handling gatt events.
 * Pretty much the same as {@link BluetoothGattCallback}, except it's an interface
 * instead of an abstract class. Some handlers commented out, because not used (yet).
 *
 * Note: the boolean return values indicate whether this callback "consumed" this event
 * or not. True means, the event was consumed by this instance and no further instances
 * shall be notified. Fallse means, this instance could not handle the event.
 */
interface GattCallback {

    /**
     * @param gatt
     * @param status
     * @param newState
     * @see BluetoothGattCallback#onConnectionStateChange(BluetoothGatt, int, int)
     */
    fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int)

    /**
     * @param gatt
     * @see BluetoothGattCallback#onServicesDiscovered(BluetoothGatt, int)
     */
    fun onServicesDiscovered(gatt: BluetoothGatt)

    /**
     * @param gatt
     * @param characteristic
     * @param status
     * @see BluetoothGattCallback#onCharacteristicRead(BluetoothGatt, BluetoothGattCharacteristic, int)
     */
    fun onCharacteristicRead(
        gatt: BluetoothGatt,
        characteristic: BluetoothGattCharacteristic,
        status: Int
    ): Boolean

    /**
     * @param gatt
     * @param characteristic
     * @param status
     * @see BluetoothGattCallback#onCharacteristicWrite(BluetoothGatt, BluetoothGattCharacteristic, int)
     */
    fun onCharacteristicWrite(
        gatt: BluetoothGatt,
        characteristic: BluetoothGattCharacteristic,
        status: Int
    ): Boolean

    /**
     * @param gatt
     * @param characteristic
     * @see BluetoothGattCallback#onCharacteristicChanged(BluetoothGatt, BluetoothGattCharacteristic)
     */
    fun onCharacteristicChanged(
        gatt: BluetoothGatt,
        characteristic: BluetoothGattCharacteristic
    ): Boolean

    /**
     * @param gatt
     * @param descriptor
     * @param status
     * @see BluetoothGattCallback#onDescriptorRead(BluetoothGatt, BluetoothGattDescriptor, int)
     */
    fun onDescriptorRead(
        gatt: BluetoothGatt,
        descriptor: BluetoothGattDescriptor,
        status: Int
    ): Boolean

    /**
     * @param gatt
     * @param descriptor
     * @param status
     * @see BluetoothGattCallback#onDescriptorWrite(BluetoothGatt, BluetoothGattDescriptor, int)
     */
    fun onDescriptorWrite(
        gatt: BluetoothGatt,
        descriptor: BluetoothGattDescriptor,
        status: Int
    ): Boolean

    /**
     * @param gatt
     * @param rssi
     * @param status
     * @see BluetoothGattCallback#onReadRemoteRssi(BluetoothGatt, int, int)
     */
    fun onReadRemoteRssi(gatt: BluetoothGatt, rssi: Int, status: Int)

    fun onMtuChanged(gatt: BluetoothGatt, mtu: Int, status: Int)
}
