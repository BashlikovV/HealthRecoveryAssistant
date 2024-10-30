package by.bashlikovvv.bluetooth.model

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import by.bashlikovvv.bluetooth.service.BtLEQueue
import by.bashlikovvv.bluetooth.transactioin.TransactionBuilder
import java.util.UUID

abstract class AbstractDeviceSupport : DeviceSupport {
    protected val device: GBDevice

    protected val queueEntitiesProvider: QueueEntitiesProvider

    abstract var mQueue: BtLEQueue?

    constructor(device: GBDevice, provider: QueueEntitiesProvider) {
        this.device = device
        this.queueEntitiesProvider = provider
    }

    override fun connect(): Boolean = false

    /**
     * If reminders can be set on the device, this method can be
     * overridden and implemented by the device support class.
     * @param reminders {@link java.util.ArrayList} containing {@link nodomain.freeyourgadget.gadgetbridge.model.Reminder} instances
     */
    override fun setReminders(reminders: List<Reminder>) {}

    /**
     * If the device supports a "find device" functionality, this method can
     * be overridden and implemented by the device support class.
     * @param start true if starting the search, false if stopping
     */
    override fun onFindDevice(start: Boolean) {}

    /**
     * @param gatt
     * @see BluetoothGattCallback#onServicesDiscovered(BluetoothGatt, int)
     */
    abstract fun onServicesDiscovered(gatt: BluetoothGatt)

    abstract fun performInitialized(taskName: String): TransactionBuilder

    abstract fun createTransactionBuilder(taskName: String): TransactionBuilder

    abstract fun getCharacteristic(uuid: UUID): BluetoothGattCharacteristic?

    abstract fun onCharacteristicChanged(
        gatt: BluetoothGatt,
        characteristic: BluetoothGattCharacteristic
    ): Boolean


    abstract fun performImmediately(builder: TransactionBuilder)
}