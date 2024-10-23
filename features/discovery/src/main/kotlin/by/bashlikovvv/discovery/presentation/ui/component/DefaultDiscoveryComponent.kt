package by.bashlikovvv.discovery.presentation.ui.component

import by.bashlikovvv.discovery.domain.contract.BluetoothReceiver
import by.bashlikovvv.discovery.domain.contract.BondingReceiver
import by.bashlikovvv.discovery.domain.model.BluetoothAction
import by.bashlikovvv.discovery.domain.model.BondAction
import by.bashlikovvv.discovery.presentation.ui.store.DiscoveryStore
import by.bashlikovvv.discovery.presentation.ui.store.DiscoveryStoreFactory
import by.bashlikovvv.ui.base.BaseComponent
import by.bashlikovvv.ui.dialog.component.AlertDialogComponent
import by.bashlikovvv.ui.dialog.component.DefaultAlertDialogComponent
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory

class DefaultDiscoveryComponent(
    componentContext: ComponentContext,
    private val storeFactory: StoreFactory,
) : DiscoveryComponent, BaseComponent(componentContext) {
    override val store: DiscoveryStore = instanceKeeper.getStore {
        DiscoveryStoreFactory(storeFactory).create()
    }

    override val bluetoothReceiver: BluetoothReceiver = BluetoothReceiver(
        onAction = { bluetoothAction -> onBluetoothAction(bluetoothAction) }
    )
    override val bondingReceiver: BondingReceiver = BondingReceiver(
        onAction = { bondAction -> onBondAction(bondAction) }
    )

    override val alertDialogComponent: AlertDialogComponent =
        DefaultAlertDialogComponent(
            componentContext = childContext(key = ALERT_DIALOG),
            storeFactory = storeFactory,
        )

    private fun onBluetoothAction(bluetoothAction: BluetoothAction) {
        store.accept(DiscoveryStore.Intent.OnBluetoothAction(bluetoothAction))
    }

    private fun onBondAction(bondAction: BondAction) {
        store.accept(DiscoveryStore.Intent.OnBondAction(bondAction))
    }

    companion object {
        const val ALERT_DIALOG = "ALERT_DIALOG"
    }
}