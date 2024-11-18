package by.bashlikovvv.bluetooth.action

import android.annotation.SuppressLint
import android.bluetooth.BluetoothGatt
import by.bashlikovvv.bluetooth.model.BtLEAction
import by.bashlikovvv.bluetooth.model.GBDevice

class CheckInitializedAction : BtLEAction {
    private val device: GBDevice

    constructor(device: GBDevice) : super(null) {
        this.device = device
    }

    override fun expectsResult(): Boolean {
        return false
    }

    @SuppressLint("MissingPermission")
    override fun run(gatt: BluetoothGatt): Boolean {
        gatt.discoverServices()
        return !shouldAbort()
    }

    private fun shouldAbort(): Boolean {
        return device.isInitialized
    }
}