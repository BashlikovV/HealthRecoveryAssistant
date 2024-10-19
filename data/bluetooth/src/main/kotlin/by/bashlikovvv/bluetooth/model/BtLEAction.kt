package by.bashlikovvv.bluetooth.model

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic

/**
 * The Bluedroid implementation only allows performing one GATT request at a time.
 * As they are asynchronous anyway, we encapsulate every GATT request (read and write)
 * inside a runnable action.
 * <p/>
 * These actions are then executed one after another, ensuring that every action's result
 * has been posted before invoking the next action.
 */
abstract class BtLEAction {
    val characteristic: BluetoothGattCharacteristic?
    val creationTimestamp: Long

    constructor(characteristic: BluetoothGattCharacteristic?) {
        this.characteristic = characteristic
        creationTimestamp = System.currentTimeMillis()
    }

    /**
     * Executes this action, e.g. reads or write a GATT characteristic.
     *
     * @param gatt the characteristic to manipulate, or null if none.
     * @return true if the action was successful, false otherwise
     */
    abstract fun run(gatt: BluetoothGatt): Boolean

    /**
     * Returns true if this action expects an (async) result which must
     * be waited for, before continuing with other actions.
     * <p/>
     * This is needed because the current Bluedroid stack can only deal
     * with one single bluetooth operation at a time.
     */
    abstract fun expectsResult(): Boolean
}