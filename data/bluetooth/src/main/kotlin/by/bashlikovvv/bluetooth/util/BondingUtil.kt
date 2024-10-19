package by.bashlikovvv.bluetooth.util

import android.content.BroadcastReceiver
import by.bashlikovvv.bluetooth.model.BondingInterface
import by.bashlikovvv.bluetooth.receivers.PairingReceiver

class BondingUtil {
    companion object {
        const val STATE_DEVICE_CANDIDATE: String = "stateDeviceCandidate"

        private const val REQUEST_CODE: Int = 1

        private const val DELAY_AFTER_BONDING: Long = 1000

        fun getPairingReceiver(activity: BondingInterface): BroadcastReceiver {
            return PairingReceiver(activity)
        }


    }
}