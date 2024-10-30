/*  Copyright (C) 2015-2024 Andreas Shimokawa, Carsten Pfeiffer, Damien
    Gaignon, Daniel Dakhno, Uwe Hermann

    This file is part of Gadgetbridge.

    Gadgetbridge is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published
    by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Gadgetbridge is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>. */
package by.bashlikovvv.bluetooth.model;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;

import java.io.IOException;
import java.util.UUID;

import by.bashlikovvv.bluetooth.service.BtLEQueue;
import by.bashlikovvv.bluetooth.transactioin.TransactionBuilder;

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
public abstract class AbstractBTLEOperation<T extends AbstractDeviceSupport>  {
    protected final T mSupport;

    private String name;

    protected AbstractBTLEOperation(T support) {
        mSupport = support;
    }

    /**
     * Performs this operation. The whole operation is asynchronous, i.e.
     * this method quickly returns before the actual operation is finished.
     * Calls #prePerform() and, if successful, #doPerform().
     *
     * @throws IOException
     */
    public final void perform() throws IOException {
        prePerform();
        doPerform();
    }

    /**
     * Hook for subclasses to perform something before #doPerform() is invoked.
     *
     * @throws IOException
     */
    protected void prePerform() throws IOException {
    }

    /**
     * Subclasses must implement this. When invoked, #prePerform() returned
     * successfully.
     * Note that subclasses HAVE TO call #operationFinished() when the entire
     * operation is done (successful or not).
     *
     * @throws IOException
     */
    protected abstract void doPerform() throws IOException;

    /**
     * You MUST call this method when the operation has finished, either
     * successfully or unsuccessfully.
     *
     * Subclasses must ensure that the {@link by.bashlikovvv.bluetooth.service.BtLEQueue queue's}'s gatt callback (set on the transaction builder by {@link #performInitialized(String)})
     * is being unset, otherwise it will continue to receive events until another transaction is being executed by the queue.
     *
     * @throws IOException
     */
    protected void operationFinished() throws IOException {
    }

    /**
     * Delegates to the DeviceSupport instance and additionally sets this instance as the Gatt
     * callback for the transaction.
     *
     * @param taskName
     * @return
     * @throws IOException
     */
    public TransactionBuilder performInitialized(String taskName) throws IOException {
        TransactionBuilder builder = mSupport.performInitialized(taskName);
        return builder;
    }

    public TransactionBuilder createTransactionBuilder(String taskName) {
        TransactionBuilder builder = getSupport().createTransactionBuilder(taskName);
        return builder;
    }

    public boolean onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        return mSupport.onCharacteristicChanged(gatt, characteristic);
    }

    protected GBDevice getDevice() {
        return mSupport.getDevice();
    }

    protected void setName(String name) {
        this.name = name;
    }

    protected BluetoothGattCharacteristic getCharacteristic(UUID uuid) {
        return mSupport.getCharacteristic(uuid);
    }

    protected BtLEQueue getQueue() {
        return mSupport.getMQueue();
    }

    public T getSupport() {
        return mSupport;
    }
}
