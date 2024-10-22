package by.bashlikovvv.discovery.presentation.ui.component

import by.bashlikovvv.discovery.domain.contract.BluetoothReceiver
import by.bashlikovvv.discovery.domain.model.BluetoothAction
import by.bashlikovvv.discovery.presentation.ui.store.DiscoveryStore
import by.bashlikovvv.discovery.presentation.ui.store.DiscoveryStoreFactory
import by.bashlikovvv.ui.base.BaseComponent
import by.bashlikovvv.ui.dialog.component.AlertDialogComponent
import by.bashlikovvv.ui.dialog.component.DefaultAlertDialogComponent
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.labels

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

    override val alertDialogComponent: AlertDialogComponent =
        DefaultAlertDialogComponent(
            componentContext = childContext(key = ALERT_DIALOG),
            storeFactory = storeFactory,
        )

    init {
        observeLabels(
            labels = store.labels,
            onLabel = { label ->
                when (label) {
                    is DiscoveryStore.Label.ShowDialog -> showAlertDialog(label)
                }
            }
        )
    }

    private fun showAlertDialog(label: DiscoveryStore.Label.ShowDialog) {
        alertDialogComponent.showDialog(
            title = label.title,
            text = label.text,
            confirmButton = label.confirmButton,
            dismissButton = label.dismissButton,
        )
    }

    private fun onBluetoothAction(bluetoothAction: BluetoothAction) {
        store.accept(
            DiscoveryStore.Intent.OnBluetoothAction(
                bluetoothAction
            )
        )
    }

    companion object {
        const val ALERT_DIALOG = "ALERT_DIALOG"
    }
}