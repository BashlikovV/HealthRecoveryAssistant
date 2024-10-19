package by.bashlikovvv.bluetooth.actions

import android.bluetooth.BluetoothGatt
import android.content.BroadcastReceiver
import android.content.Context
import by.bashlikovvv.bluetooth.model.BondingInterface
import by.bashlikovvv.bluetooth.model.BtLEAction
import by.bashlikovvv.bluetooth.model.GBDeviceCandidate
import by.bashlikovvv.bluetooth.util.BondingUtil

class BondAction : BtLEAction, BondingInterface {
    constructor() : super(null)

    private var macAddress: String? = null

    private val pairingReceiver: BroadcastReceiver = BondingUtil.getPairingReceiver(this)

    override fun run(gatt: BluetoothGatt): Boolean {
        macAddress = gatt.device.address


        return true
    }

    override fun expectsResult(): Boolean = false

    override fun onBondingComplete(success: Boolean) {
        unregisterBroadcastReceivers()
    }

    override fun getCurrentTarget(): GBDeviceCandidate {
        throw IllegalStateException()
    }

    override fun unregisterBroadcastReceivers() {

    }

    override fun getMacAddress(): String {
        return macAddress ?: ""
    }

    override fun getAttemptToConnect(): Boolean = false

    override fun registerBroadcastReceivers() {

    }

    override fun getContext(): Context {
        TODO("Not yet implemented")
    }
}