package by.bashlikovvv.bluetooth.transactioin

import android.bluetooth.BluetoothGattCharacteristic
import by.bashlikovvv.bluetooth.action.WriteAction
import by.bashlikovvv.bluetooth.model.BtLEAction
import by.bashlikovvv.bluetooth.model.Transaction
import by.bashlikovvv.bluetooth.service.BtLEQueue

class TransactionBuilder {
    private val transaction: Transaction

    constructor(taskName: String) {
        transaction = Transaction(taskName)
    }

    fun write(
        characteristic: BluetoothGattCharacteristic?,
        data: ByteArray,
    ): TransactionBuilder {
        return characteristic?.let { add(WriteAction(characteristic, data)) } ?: this
    }

    fun queue(queue: BtLEQueue) {
        queue.add(transaction)
    }

    private fun add(action: BtLEAction): TransactionBuilder {
        transaction.add(action)
        return this
    }
}