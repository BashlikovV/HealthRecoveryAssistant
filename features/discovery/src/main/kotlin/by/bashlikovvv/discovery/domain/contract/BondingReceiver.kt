package by.bashlikovvv.discovery.domain.contract

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import by.bashlikovvv.discovery.domain.model.BondAction
import by.bashlikovvv.util.getBluetoothDeviceFromIntent

class BondingReceiver(
    private val onAction: (BondAction) -> Unit,
) : BroadcastReceiver() {
    private val actions = mapOf<Int, ((Intent) -> Unit)?>(
        BondAction.Bonded.NAME to ::onBonded,
        BondAction.Bonding.NAME to ::onBonding,
        BondAction.None.NAME to ::onNone,
    )

    override fun onReceive(context: Context?, intent: Intent?) {
        if (BluetoothDevice.ACTION_BOND_STATE_CHANGED == intent?.action) {
            val bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, DEFAULT_STATE)
            actions.getOrElse(bondState.toInt()) { null }
                ?.invoke(intent) ?: onAction(BondAction.Default)
        }
    }

    private fun onBonded(intent: Intent) {
        intent.getBluetoothDeviceFromIntent()?.let {
            onAction(BondAction.Bonded(it))
        }
    }

    private fun onBonding(intent: Intent) {
        intent.getBluetoothDeviceFromIntent()?.let {
            onAction(BondAction.Bonding(it))
        }
    }

    private fun onNone(intent: Intent) {
        intent.getBluetoothDeviceFromIntent()?.let {
            onAction(BondAction.None(it))
        }
    }

    companion object {
        const val DEFAULT_STATE = -1
    }
}