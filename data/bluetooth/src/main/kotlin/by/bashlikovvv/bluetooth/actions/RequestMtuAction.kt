@file:SuppressLint("MissingPermission")
package by.bashlikovvv.bluetooth.actions

import android.annotation.SuppressLint
import android.bluetooth.BluetoothGatt
import by.bashlikovvv.bluetooth.model.BtLEAction

class RequestMtuAction : BtLEAction {
    private val mtu: Int

    constructor(mtu: Int) : super(null) {
        this.mtu = mtu
    }

    override fun run(gatt: BluetoothGatt): Boolean = gatt.requestMtu(mtu)

    override fun expectsResult(): Boolean = true
}