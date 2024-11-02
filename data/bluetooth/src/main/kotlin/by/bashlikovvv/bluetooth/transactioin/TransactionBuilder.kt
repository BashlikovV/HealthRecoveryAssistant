package by.bashlikovvv.bluetooth.transactioin

import android.bluetooth.BluetoothGattCharacteristic
import by.bashlikovvv.bluetooth.action.NotifyAction
import by.bashlikovvv.bluetooth.action.ReadAction
import by.bashlikovvv.bluetooth.action.WriteAction
import by.bashlikovvv.bluetooth.model.BtLEAction
import by.bashlikovvv.bluetooth.model.GattCallback
import by.bashlikovvv.bluetooth.model.Transaction
import by.bashlikovvv.bluetooth.service.BtLEQueue
import kotlin.experimental.or

class TransactionBuilder {
    val transaction: Transaction

    constructor(taskName: String) {
        transaction = Transaction(taskName)
    }

    fun write(
        characteristic: BluetoothGattCharacteristic?,
        data: ByteArray,
    ): TransactionBuilder {
        return characteristic?.let { add(WriteAction(characteristic, data)) } ?: this
    }

    fun writeToChunkedOld(
        characteristic: BluetoothGattCharacteristic?,
        type: Int,
        data: ByteArray
    ): TransactionBuilder {
        val maxChunkLength = MIN_MTU - 6
        var remaining = data.size
        var count = 0
        while (remaining > 0) {
            val copyBytes = remaining.coerceAtMost(maxChunkLength)
            val chunk = ByteArray(copyBytes + 3)
            var flags: Byte = 0
            if (remaining <= maxChunkLength) {
                flags = flags or 0x80.toByte()
                if (count == 0) {
                    flags = flags or 0x40
                }
            } else if (count > 0) {
                flags = flags or 0x40
            }

            chunk[0] = 0
            chunk[1] = (flags or type.toByte())
            chunk[2] = (count and 0xff).toByte()

            System.arraycopy(data, count++ * maxChunkLength, chunk, 3, copyBytes)
            write(characteristic, chunk)
            remaining -= copyBytes
        }
        return this
    }

    fun queue(queue: BtLEQueue) {
        queue.add(transaction)
    }

    fun add(action: BtLEAction): TransactionBuilder {
        transaction.add(action)
        return this
    }

    fun notify(characteristic: BluetoothGattCharacteristic?, enable: Boolean): TransactionBuilder {
        if (characteristic == null) return this
        return add(createNotifyAction(characteristic, enable))
    }

    fun read(characteristic: BluetoothGattCharacteristic): TransactionBuilder {
        return add(ReadAction(characteristic))
    }

    fun setCallback(callback: GattCallback) {
        transaction.setCallback(callback)
    }

    private fun createNotifyAction(
        characteristic: BluetoothGattCharacteristic,
        enable: Boolean
    ): NotifyAction {
        return NotifyAction(characteristic, enable)
    }

    companion object {
        const val MIN_MTU = 23
    }
}