package by.bashlikovvv.bluetooth.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import by.bashlikovvv.bluetooth.model.BondingInterface
import by.bashlikovvv.bluetooth.model.GBDevice

class PairingReceiver(
    private val activity: BondingInterface
) : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (GBDevice.ACTION_DEVICE_CHANGED == intent?.action) {
            val device = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(GBDevice.EXTRA_DEVICE, GBDevice::class.java)
            } else {
                intent.getParcelableExtra(GBDevice.EXTRA_DEVICE)
            }
            if (activity.getMacAddress() == device?.address) {
                if (device.isInitialized) {
                    activity.onBondingComplete(true)
                }
            }
        }
    }
}