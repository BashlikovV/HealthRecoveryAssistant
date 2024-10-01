package by.bashlikovvv.root.presentation.ui.store

import by.bashlikovvv.root.presentation.ui.store.RootStore.Intent
import by.bashlikovvv.root.presentation.ui.store.RootStore.Label
import by.bashlikovvv.root.presentation.ui.store.RootStore.State
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory

class RootStoreFactory(
    private val storeFactory: StoreFactory,
) {
    fun create(): RootStore =
        object : RootStore, Store<Intent, State, Label> by storeFactory.create(
            name = STORE_NAME,
            initialState = State(),
            executorFactory = ::RootStoreExecutor,
            reducer = reducerImpl,
        ) { }

    sealed class Msg {
        data class Error(val th: Throwable) : Msg()
    }

    private val reducerImpl =
        Reducer<State, Msg> { msg ->
            when(msg) {
                else -> this
            }
        }

    companion object {
        private const val STORE_NAME = "RootStore"
    }
}