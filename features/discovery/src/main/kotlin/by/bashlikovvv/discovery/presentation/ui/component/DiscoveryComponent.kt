package by.bashlikovvv.discovery.presentation.ui.component

import by.bashlikovvv.discovery.domain.contract.BluetoothReceiver
import by.bashlikovvv.discovery.domain.contract.BondingReceiver
import by.bashlikovvv.discovery.presentation.ui.store.DiscoveryStore
import by.bashlikovvv.ui.dialog.component.AlertDialogComponent

interface DiscoveryComponent {
    val store: DiscoveryStore

    val bluetoothReceiver: BluetoothReceiver

    val bondingReceiver: BondingReceiver

    val alertDialogComponent: AlertDialogComponent
}