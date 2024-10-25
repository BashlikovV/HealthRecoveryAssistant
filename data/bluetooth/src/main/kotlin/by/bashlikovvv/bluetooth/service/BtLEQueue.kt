package by.bashlikovvv.bluetooth.service

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import by.bashlikovvv.bluetooth.model.AbstractTransaction
import by.bashlikovvv.bluetooth.model.GBDevice
import by.bashlikovvv.bluetooth.model.QueueEntitiesProvider
import by.bashlikovvv.bluetooth.model.Transaction
import java.util.UUID
import java.util.concurrent.CountDownLatch
import java.util.concurrent.LinkedBlockingQueue
import kotlin.concurrent.thread

class BtLEQueue(
    private val device: GBDevice,
    private val queueEntitiesProvider: QueueEntitiesProvider,
) {
    private val adapter: BluetoothAdapter
        get() = queueEntitiesProvider.getAdapter() ?: BluetoothAdapter.getDefaultAdapter()

    private val transactions = LinkedBlockingQueue<AbstractTransaction>()

    private var isDisposed: Boolean = false

    private var isCrashed: Boolean = false
    
    private var bluetoothGatt: BluetoothGatt? = null
    
    private var waitForActionResultLatch: CountDownLatch? = null
    
    private val gattMonitor = Any()

    private var characteristics: Map<UUID, List<BluetoothGattCharacteristic>> = emptyMap()

    private var waitCharacteristic: BluetoothGattCharacteristic? = null

    init {
        thread {
            while (!isDisposed && !isCrashed) {
                try {
                    val transaction = transactions.take()
                    if (transaction is Transaction) {
                        for (action in transaction.actions) {
                            waitCharacteristic = action.characteristic
                            waitForActionResultLatch = CountDownLatch(1)
                            if (bluetoothGatt?.let { action.run(it) } == true) {
                                if (action.expectsResult()) {
                                    waitForActionResultLatch?.await()
                                    waitForActionResultLatch = null
                                }
                            }
                        }
                    }
                } catch (_: Throwable) {
                    isCrashed = true
                } finally {
                    waitForActionResultLatch = null
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun connect(): Boolean {
        synchronized(gattMonitor) {
            if (bluetoothGatt != null) {
                disconnect()
            }
        }

        val remoteDevice = adapter.getRemoteDevice(device.device.address)
        synchronized(gattMonitor) {
            val gatt = queueEntitiesProvider.connectGatt(remoteDevice, callback())
            bluetoothGatt = gatt
            Thread.sleep(300)
            gatt.discoverServices()
        }
        
        return bluetoothGatt != null
    }

    private fun callback(): BluetoothGattCallback = object : BluetoothGattCallback() {
        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            val characteristics = bluetoothGatt?.services?.flatMap { service ->
                service.characteristics
            } ?: emptyList()
            characteristics
                .groupBy { it.uuid }
                .ifEmpty { null }
                ?.let { this@BtLEQueue.characteristics = it }
        }

        override fun onMtuChanged(gatt: BluetoothGatt?, mtu: Int, status: Int) {
            waitForActionResultLatch?.countDown()
        }

        override fun onCharacteristicWrite(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            checkWaitingCharacteristic(characteristic)
        }
    }

    @SuppressLint("MissingPermission")
    fun disconnect() {
        synchronized(gattMonitor) {
            bluetoothGatt?.disconnect()
            bluetoothGatt?.close()
            bluetoothGatt = null
        }
    }

    fun add(transaction: AbstractTransaction) {
        transactions.add(transaction)
    }
    
    @SuppressLint("MissingPermission")
    fun getCharacteristic(uuid: UUID): BluetoothGattCharacteristic? {
        return characteristics
            .getOrElse(uuid) { null }
            ?.first()
    }

    private fun checkWaitingCharacteristic(
        characteristic: BluetoothGattCharacteristic?,
    ) {
        if (characteristic != null && waitCharacteristic != null && characteristic.uuid == waitCharacteristic?.uuid) {
            waitForActionResultLatch?.countDown()
        }
    }
}