package by.bashlikovvv.discovery.domain.model

import android.bluetooth.BluetoothAdapter.ACTION_DISCOVERY_STARTED
import android.bluetooth.BluetoothAdapter.ACTION_STATE_CHANGED
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothDevice.ACTION_BOND_STATE_CHANGED
import android.bluetooth.BluetoothDevice.ACTION_FOUND
import android.bluetooth.BluetoothDevice.ACTION_UUID
import android.os.ParcelUuid

sealed interface BluetoothAction {
    data object DiscoveryStarted : BluetoothAction {
        const val NAME: String = ACTION_DISCOVERY_STARTED
    }

    /**
     * @param state [android.bluetooth.BluetoothAdapter.getState]
     * */
    data class StateChanged(
        val state: Int,
    ) : BluetoothAction {
        companion object {
            const val NAME: String = ACTION_STATE_CHANGED
        }
    }

    /**
     * @param device [BluetoothDevice]
     * @param rssi [BluetoothDevice.EXTRA_RSSI]
     * */
    data class Found(
        val device: BluetoothDevice?,
        val rssi: Short?,
    ) : BluetoothAction {
        companion object {
            const val NAME: String = ACTION_FOUND
            const val DEFAULT_RSSI: Short = -1
        }
    }

    /**
     * @param device [BluetoothDevice]
     * @param rssi [BluetoothDevice.EXTRA_RSSI]
     * @param uuids [BluetoothDevice.EXTRA_UUID]
     * */
    data class UUID(
        val device: BluetoothDevice?,
        val rssi: Short?,
        val uuids: Array<ParcelUuid>?,
    ) : BluetoothAction {
        companion object {
            const val NAME: String = ACTION_UUID
        }
    }

    /**
    * @param device [BluetoothDevice]
    * */
    data class BondStateChanged(
        val device: BluetoothDevice?,
    ) : BluetoothAction {
        companion object {
            const val NAME: String = ACTION_BOND_STATE_CHANGED
        }
    }
}