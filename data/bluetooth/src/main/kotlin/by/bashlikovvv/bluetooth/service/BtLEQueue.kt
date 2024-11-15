package by.bashlikovvv.bluetooth.service

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothProfile
import by.bashlikovvv.bluetooth.model.AbstractTransaction
import by.bashlikovvv.bluetooth.model.GBDevice
import by.bashlikovvv.bluetooth.model.GattCallback
import by.bashlikovvv.bluetooth.model.QueueEntitiesProvider
import by.bashlikovvv.bluetooth.model.Transaction
import java.util.UUID
import java.util.concurrent.CountDownLatch
import java.util.concurrent.LinkedBlockingQueue
import kotlin.concurrent.thread

class BtLEQueue(
    private val device: GBDevice,
    private val queueEntitiesProvider: QueueEntitiesProvider,
    callback: GattCallback,
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
                        internalGattCallback.setTransactionGattCallback(transaction.callback)
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
            val gatt = queueEntitiesProvider.connectGatt(remoteDevice, internalGattCallback)
            bluetoothGatt = gatt
            Thread.sleep(300)
            gatt.discoverServices()
        }
        
        return bluetoothGatt?.connect() == true
    }

    private val internalGattCallback = object : BluetoothGattCallback() {
        private var transactionGattCallback: GattCallback? = null

        private val externalGattCallback: GattCallback = callback

        fun setTransactionGattCallback(callback: GattCallback?) {
            transactionGattCallback = callback
        }

        val callback: GattCallback
            get() {
                if (transactionGattCallback != null) {
                    return transactionGattCallback!!
                }

                return externalGattCallback
            }

        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            gatt?.let { gattNotNull ->
                this.callback.onConnectionStateChange(gattNotNull, status, newState)
            }
            when(newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    device.setState(GBDevice.State.CONNECTED)
                }
                BluetoothProfile.STATE_DISCONNECTED -> handleDisconnect(status)
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            gatt?.let { this.callback.onServicesDiscovered(it) }
            val characteristics = gatt?.services?.flatMap { service ->
                service.characteristics
            } ?: emptyList()
            characteristics
                .groupBy { it.uuid }
                .ifEmpty { null }
                ?.let { this@BtLEQueue.characteristics = it }
        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray,
            status: Int
        ) {
            this.callback.onCharacteristicRead(gatt, characteristic, status)
            checkWaitingCharacteristic(characteristic)
        }

        override fun onCharacteristicWrite(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            gatt?.let { gattNotNull ->
                characteristic?.let { characteristicNotNull ->
                    this.callback.onCharacteristicWrite(gattNotNull, characteristicNotNull, status)
                }
            }
            checkWaitingCharacteristic(characteristic)
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray
        ) {
            this.callback.onCharacteristicChanged(gatt, characteristic)
            checkWaitingCharacteristic(characteristic)
        }

        override fun onDescriptorRead(
            gatt: BluetoothGatt,
            descriptor: BluetoothGattDescriptor,
            status: Int,
            value: ByteArray
        ) {
            this.callback.onDescriptorRead(gatt, descriptor, status)
            checkWaitingCharacteristic(descriptor.characteristic)
        }

        override fun onDescriptorWrite(
            gatt: BluetoothGatt?,
            descriptor: BluetoothGattDescriptor?,
            status: Int
        ) {
            gatt?.let { gattNotNull ->
                descriptor?.let { descriptorNotNull ->
                    this.callback.onDescriptorWrite(gattNotNull, descriptorNotNull, status)
                }
            }
            onCharacteristicWrite(gatt, descriptor?.characteristic, status)
            checkWaitingCharacteristic(descriptor?.characteristic)
        }

        override fun onReadRemoteRssi(gatt: BluetoothGatt?, rssi: Int, status: Int) {
            gatt?.let { gattNotNull ->
                this.callback.onReadRemoteRssi(gattNotNull, rssi, status)
            }
        }

        override fun onMtuChanged(gatt: BluetoothGatt, mtu: Int, status: Int) {
            this.callback.onMtuChanged(gatt, mtu, status)
            waitForActionResultLatch?.countDown()
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

    fun insert(transaction: Transaction) {
        val tail = ArrayList<AbstractTransaction>(transactions.size + 2)
        tail.addAll(transactions)
        transactions.clear()
        transactions.add(transaction)
        transactions.addAll(tail)
    }

    private fun checkWaitingCharacteristic(
        characteristic: BluetoothGattCharacteristic?,
    ) {
        if (characteristic != null && waitCharacteristic != null && characteristic.uuid == waitCharacteristic?.uuid) {
            waitForActionResultLatch?.countDown()
        }
    }

    private fun handleDisconnect(status: Int) {
        waitForActionResultLatch?.countDown()
        device.setState(GBDevice.State.NOT_CONNECTED)
        connect()
    }
}