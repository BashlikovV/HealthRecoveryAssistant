package by.bashlikovvv.bluetooth.model

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import by.bashlikovvv.bluetooth.action.CheckInitializedAction
import by.bashlikovvv.bluetooth.service.BtLEQueue
import by.bashlikovvv.bluetooth.transactioin.TransactionBuilder
import java.util.UUID
import kotlin.collections.ifEmpty

abstract class AbstractBtLEDeviceSupport(
    device: GBDevice,
    provider: QueueEntitiesProvider,
) : AbstractDeviceSupport(device, provider) {
    override var mQueue: BtLEQueue? = null

    private var availableCharacteristics: Map<UUID, List<BluetoothGattCharacteristic>>? = null

    private val supportedDevices: MutableSet<UUID> = mutableSetOf()

    private val characteristicsMonitor = Any()

    protected open val isInitialized: Boolean
        get() = device.isInitialized

    private val mtu = 23

    override fun connect(): Boolean {
        if (mQueue == null) {
            mQueue = BtLEQueue(
                device = device,
                queueEntitiesProvider = queueEntitiesProvider,
            )
        }

        return mQueue?.connect() == true
    }

    open fun disconnect() {
        if (mQueue != null) {
            mQueue?.disconnect()
        }
    }

    protected open fun initializeDevice(builder: TransactionBuilder): TransactionBuilder = builder

    override fun createTransactionBuilder(taskName: String): TransactionBuilder {
        return TransactionBuilder(taskName)
    }

    override fun performInitialized(taskName: String): TransactionBuilder {
        if (isInitialized) {
            mQueue?.let { queueNotNull ->
                val builder = createTransactionBuilder("Initialize device")
                builder.add(CheckInitializedAction(device))
                initializeDevice(builder).queue(queueNotNull)
            }
        }
        return createTransactionBuilder(taskName)
    }

    fun getQueue(): BtLEQueue? = mQueue

    override fun getCharacteristic(uuid: UUID): BluetoothGattCharacteristic? {
        synchronized(characteristicsMonitor) {
            return availableCharacteristics
                ?.getOrElse(uuid) { null }
                ?.first()
        }
    }

    override fun onCharacteristicChanged(
        gatt: BluetoothGatt,
        characteristic: BluetoothGattCharacteristic
    ): Boolean { return false }

    override fun onServicesDiscovered(gatt: BluetoothGatt) {
        val characteristics = gatt.services?.flatMap { service ->
            service.characteristics
        } ?: emptyList()
        characteristics
            .groupBy { it.uuid }
            .ifEmpty { null }
            ?.let { this@AbstractBtLEDeviceSupport.availableCharacteristics = it }
    }

    override fun performImmediately(builder: TransactionBuilder) {
        mQueue?.insert(builder.transaction)
    }

    fun getMtu() = mtu

    companion object {
        const val BASE_UUID = "0000%s-0000-1000-8000-00805f9b34fb"
    }
}