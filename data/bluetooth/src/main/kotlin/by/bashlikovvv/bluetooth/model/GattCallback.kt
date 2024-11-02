/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package by.bashlikovvv.bluetooth.model

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor

/**
 * Callback interface handling gatt events.
 * Pretty much the same as [BluetoothGattCallback], except it's an interface
 * instead of an abstract class. Some handlers commented out, because not used (yet).
 *
 * Note: the boolean return values indicate whether this callback "consumed" this event
 * or not. True means, the event was consumed by this instance and no further instances
 * shall be notified. False means, this instance could not handle the event.
 */
interface GattCallback {

    /**
     * @param gatt
     * @param status
     * @param newState
     * @see BluetoothGattCallback.onConnectionStateChange
     */
    fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int)

    /**
     * @param gatt
     * @see BluetoothGattCallback.onServicesDiscovered
     */
    fun onServicesDiscovered(gatt: BluetoothGatt)

    /**
     * @param gatt
     * @param characteristic
     * @param status
     * @see BluetoothGattCallback.onCharacteristicRead
     */
    fun onCharacteristicRead(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int): Boolean

    /**
     * @param gatt
     * @param characteristic
     * @param status
     * @see BluetoothGattCallback.onCharacteristicWrite
     */
    fun onCharacteristicWrite(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int): Boolean

    /**
     * @param gatt
     * @param characteristic
     * @see BluetoothGattCallback.onCharacteristicChanged
     */
    fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic): Boolean

    /**
     * @param gatt
     * @param descriptor
     * @param status
     * @see BluetoothGattCallback.onDescriptorRead
     */
    fun onDescriptorRead(gatt: BluetoothGatt, descriptor: BluetoothGattDescriptor, status: Int): Boolean

    /**
     * @param gatt
     * @param descriptor
     * @param status
     * @see BluetoothGattCallback.onDescriptorWrite
     */
    fun onDescriptorWrite(gatt: BluetoothGatt, descriptor: BluetoothGattDescriptor, status: Int): Boolean

    /**
     * @param gatt
     * @param rssi
     * @param status
     * @see BluetoothGattCallback.onReadRemoteRssi
     */
    fun onReadRemoteRssi(gatt: BluetoothGatt, rssi: Int, status: Int)

    fun onMtuChanged(gatt: BluetoothGatt, mtu: Int, status: Int)
}

