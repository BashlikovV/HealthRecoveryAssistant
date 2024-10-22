package by.bashlikovvv.ui.dialog.component

import by.bashlikovvv.ui.base.BaseComponent
import by.bashlikovvv.ui.dialog.store.AlertDialogStore
import by.bashlikovvv.ui.dialog.store.AlertDialogStoreFactory
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory

class DefaultAlertDialogComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
) : AlertDialogComponent, BaseComponent(componentContext) {
    override val store: AlertDialogStore = instanceKeeper.getStore {
        AlertDialogStoreFactory(storeFactory).create()
    }

    override fun showDialog(
        title: String,
        text: String,
        confirmButton: String?,
        dismissButton: String?,
    ) = store.accept(
        AlertDialogStore.Intent.ShowDialog(
            title = title,
            text = text,
            confirmButton = confirmButton,
            dismissButton = dismissButton,
        )
    )
}