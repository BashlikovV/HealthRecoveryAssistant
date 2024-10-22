package by.bashlikovvv.ui.dialog.store

import by.bashlikovvv.ui.base.BaseStoreFactory
import by.bashlikovvv.ui.dialog.store.AlertDialogStore.*
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory

class AlertDialogStoreFactory(
    storeFactory: StoreFactory
) : BaseStoreFactory<AlertDialogStore>(storeFactory) {
    override fun create(): AlertDialogStore =
        object : AlertDialogStore, Store<Intent, State, Nothing> by storeFactory.create(
            name = STORE_NAME,
            initialState = State(),
            executorFactory = ::AlertDialogExecutor,
            reducer = reducerImpl,
        ) { }

    private val reducerImpl =
        Reducer<State, Msg> { msg ->
            when (msg) {
                is Msg.ShowDialog -> copy(
                    isVisible = true,
                    title = msg.title,
                    text = msg.text,
                    confirmButton = msg.confirmButton,
                    dismissButton = msg.dismissButton,
                )
                Msg.HideDialog -> copy(isVisible = false)
            }
        }

    internal sealed class Msg {
        data class ShowDialog(
            val title: String = "",
            val text: String = "",
            val confirmButton: String? = null,
            val dismissButton: String? = null,
        ) : Msg()
        object HideDialog : Msg()
    }

    companion object {
        private const val STORE_NAME = "AlertDialogStore"
    }
}