package by.bashlikovvv.bluetooth.model

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import by.bashlikovvv.bluetooth.transactioin.TransactionBuilder
import java.util.UUID

/**
 * Abstract base class for a BTLEOperation, i.e. an operation that does more than
 * just sending a few bytes to the device. It typically involves exchanging many messages
 * between the mobile and the device.
 * <p/>
 * One operation may execute multiple @{link Transaction transactions} with each
 * multiple @{link BTLEAction actions}.
 * <p/>
 * This class implements GattCallback so that subclasses may override those methods
 * to handle those events.
 * Note: by default all Gatt events are forwarded to AbstractBTLEDeviceSupport, subclasses may override
 * this behavior.
 */
abstract class AbstractBTLEOperation<T : AbstractDeviceSupport>(protected val mSupport: T) {

    private var name: String? = null

    val support: T
        get() = mSupport

    val device: GBDevice
        get() = support.device

    /**
     * Performs this operation. The whole operation is asynchronous, i.e.
     * this method quickly returns before the actual operation is finished.
     * Calls #prePerform() and, if successful, #doPerform().
     *
     */
    fun perform() {
        prePerform()
        doPerform()
    }

    /**
     * Hook for subclasses to perform something before #doPerform() is invoked.
     *
     */
    protected open fun prePerform() {}

    /**
     * Subclasses must implement this. When invoked, #prePerform() returned
     * successfully.
     * Note that subclasses HAVE TO call #operationFinished() when the entire
     * operation is done (successful or not).
     *
     */
    abstract fun doPerform()

    /**
     * You MUST call this method when the operation has finished, either
     * successfully or unsuccessfully.
     *
     * Subclasses must ensure that the {@link by.bashlikovvv.bluetooth.service.BtLEQueue queue's}'s gatt callback (set on the transaction builder by {@link #performInitialized(String)})
     * is being unset, otherwise it will continue to receive events until another transaction is being executed by the queue.
     *
     */
    protected open fun operationFinished() {}

    /**
     * Delegates to the DeviceSupport instance and additionally sets this instance as the Gatt
     * callback for the transaction.
     *
     * @param taskName
     * @return
     */
    fun performInitialized(taskName: String): TransactionBuilder {
        return mSupport.performInitialized(taskName)
    }

    fun createTransactionBuilder(taskName: String): TransactionBuilder {
        return mSupport.createTransactionBuilder(taskName)
    }

    open fun onCharacteristicChanged(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?): Boolean {
        return gatt?.let { gatt ->
            characteristic?.let { characteristic ->
                mSupport.onCharacteristicChanged(gatt, characteristic)
            }
        } == true
    }

    protected fun setName(name: String) {
        this.name = name
    }

    protected fun getCharacteristic(uuid: UUID): BluetoothGattCharacteristic? {
        return mSupport.getCharacteristic(uuid)
    }
}

