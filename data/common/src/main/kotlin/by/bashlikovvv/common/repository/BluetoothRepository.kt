package by.bashlikovvv.common.repository

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGattCallback
import by.bashlikovvv.bluetooth.model.GBDevice
import by.bashlikovvv.bluetooth.model.QueueEntitiesProvider
import by.bashlikovvv.bluetooth.service.BtLEQueue
import by.bashlikovvv.bluetooth.transactioin.TransactionBuilder
import java.util.UUID

class BluetoothRepository(
    private val queueEntitiesProvider: QueueEntitiesProvider,
) {
    private var queue: BtLEQueue? = null

    fun connect(
        device: BluetoothDevice,
        callback: BluetoothGattCallback,
    ): Boolean {
        val tmpQueue = BtLEQueue(
            device = GBDevice(
                device = device,
            ),
            callback = callback,
            queueEntitiesProvider = queueEntitiesProvider,
        )
        queue = tmpQueue

        return tmpQueue.connect()
    }

    fun disconnect() {
        queue?.disconnect()
    }

    fun sendFindDeviceCommand(start: Boolean) {
        queue?.let { queueNotNull ->
            val characteristics = queueNotNull.getCharacteristic(UUID_CHARACTERISTIC_ALERT_LEVEL)
            val tb = TransactionBuilder("find device")
            tb.write(characteristics, if (start) byteArrayOf(3) else byteArrayOf(0))
            tb.queue(queueNotNull)
        }
    }

    companion object {
        const val BASE_UUID = "0000%s-0000-1000-8000-00805f9b34fb"
        val UUID_CHARACTERISTIC_ALERT_LEVEL = UUID.fromString(String.format(BASE_UUID, "2A06"))
    }
}