package by.bashlikovvv.ui.dialog.store

import by.bashlikovvv.ui.base.BaseCoroutineExecutor
import by.bashlikovvv.ui.dialog.store.AlertDialogStore.*
import by.bashlikovvv.ui.dialog.store.AlertDialogStoreFactory.Msg

internal class AlertDialogExecutor : BaseCoroutineExecutor<Intent, Nothing, State, Msg, Nothing>() {
    override fun executeIntent(intent: Intent, getState: () -> State) {
        when(intent) {
            is Intent.HideDialog -> dispatch(Msg.HideDialog)
            is Intent.ShowDialog -> dispatch(
                Msg.ShowDialog(
                    title = intent.title,
                    text = intent.text,
                    confirmButton = intent.confirmButton,
                    dismissButton = intent.dismissButton,
                )
            )
        }
    }
}