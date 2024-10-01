package by.bashlikovvv.home.presentation.ui.store

import by.bashlikovvv.home.presentation.ui.store.HomeStore.*
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory

class HomeStoreFactory(
    private val storeFactory: StoreFactory,
) {
    fun create(): HomeStore = object : HomeStore, Store<Intent, State, Label> by storeFactory.create(
        name = "",
        autoInit = false,
        initialState = State(),
        executorFactory = ::HomeStoreExecutor,
        reducer = reducerImpl
    ) { }

    private val reducerImpl =
        Reducer<State, Msg> { msg ->
            when (msg) {
                else -> this
            }
        }

    sealed class Msg { }

    sealed class Action {
        data object Init : Action()
    }
}