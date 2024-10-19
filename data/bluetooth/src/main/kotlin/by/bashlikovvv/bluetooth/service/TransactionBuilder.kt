package by.bashlikovvv.bluetooth.service

import android.bluetooth.BluetoothGattCharacteristic
import by.bashlikovvv.bluetooth.actions.BondAction
import by.bashlikovvv.bluetooth.actions.ReadAction
import by.bashlikovvv.bluetooth.actions.RequestMtuAction
import by.bashlikovvv.bluetooth.actions.WriteAction
import by.bashlikovvv.bluetooth.model.BtLEAction
import by.bashlikovvv.bluetooth.model.Transaction
import java.util.Arrays

/**
 * Invokes a write operation on a given GATT characteristic.
 * The result status will be made available asynchronously through the
 * {@link BluetoothGattCallback}
 */
class TransactionBuilder {
    private val transaction: Transaction

    constructor(taskName: String) {
        this.transaction = Transaction(taskName)
    }

    fun read(characteristics: BluetoothGattCharacteristic): TransactionBuilder {
        return add(ReadAction(characteristics))
    }

    fun write(characteristic: BluetoothGattCharacteristic, data: ByteArray): TransactionBuilder {
        return add(WriteAction(characteristic, data))
    }

    fun writeChunkedData(
        characteristic: BluetoothGattCharacteristic,
        data: ByteArray,
        chunkSize: Int
    ): TransactionBuilder {
        for (start in 0..<data.size step chunkSize) {
            var end = start + chunkSize
            if (end > data.size) end = data.size
            add(WriteAction(characteristic, data.copyOfRange(start, end)))
        }

        return this
    }

    fun requestMtu(mtu: Int): TransactionBuilder {
        return add(RequestMtuAction(mtu))
    }

    fun bond(): TransactionBuilder {
        return add(BondAction())
    }

    fun add(action: BtLEAction): TransactionBuilder = also { transaction.add(action) }
}