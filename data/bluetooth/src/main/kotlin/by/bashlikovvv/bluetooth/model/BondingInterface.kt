package by.bashlikovvv.bluetooth.model

import android.content.Context

interface BondingInterface {
    fun onBondingComplete(success: Boolean)

    fun getCurrentTarget(): GBDeviceCandidate

    fun unregisterBroadcastReceivers()

    fun getMacAddress(): String

    fun getAttemptToConnect(): Boolean

    fun registerBroadcastReceivers()

    fun getContext(): Context
}