package by.bashlikovvv.discovery.domain.model

import android.bluetooth.BluetoothDevice

sealed interface BondAction {
    data class Bonded(val device: BluetoothDevice) : BondAction {
        companion object {
            const val NAME = BluetoothDevice.BOND_BONDED
        }
    }

    data class None(val device: BluetoothDevice) : BondAction {
        companion object {
            const val NAME = BluetoothDevice.BOND_NONE
        }
    }

    data class Bonding(val device: BluetoothDevice) : BondAction {
        companion object {
            const val NAME = BluetoothDevice.BOND_BONDING
        }
    }

    data object Default : BondAction
}