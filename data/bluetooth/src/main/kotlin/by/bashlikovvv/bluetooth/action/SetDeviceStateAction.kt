package by.bashlikovvv.bluetooth.action

import android.bluetooth.BluetoothGatt
import by.bashlikovvv.bluetooth.model.BtLEAction
import by.bashlikovvv.bluetooth.model.GBDevice

class SetDeviceStateAction : BtLEAction {
    private val device: GBDevice

    private val state: GBDevice.State

    constructor(
        device: GBDevice,
        state: GBDevice.State,
    ) : super(null) {
        this.device = device
        this.state = state
    }

    override fun run(gatt: BluetoothGatt): Boolean {
        device.setState(state)
        return true
    }

    override fun expectsResult(): Boolean = false
}
