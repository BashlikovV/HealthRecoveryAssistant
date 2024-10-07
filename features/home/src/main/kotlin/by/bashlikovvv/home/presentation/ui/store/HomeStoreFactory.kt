package by.bashlikovvv.home.presentation.ui.store

import by.bashlikovvv.domain.model.WearableEvents
import by.bashlikovvv.home.presentation.ui.store.HomeStore.*
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory

class HomeStoreFactory(
    private val storeFactory: StoreFactory,
) {
    fun create(): HomeStore = object : HomeStore, Store<Intent, State, Label> by storeFactory.create(
        name = STORE_NAME,
        initialState = State(),
        autoInit = true,
        executorFactory = ::HomeStoreExecutor,
        reducer = reducerImpl
    ) { }

    private val reducerImpl =
        Reducer<State, Msg> { msg ->
            when (msg) {
                is Msg.HRAFileData -> this.copy(fileContent = msg.data, fileName = msg.name)
            }
        }

    sealed class Msg {
        data class HRAFileData(
            val name: String,
            val data: WearableEvents?,
        ) : Msg()
    }

    companion object {
        const val STORE_NAME ="HomeStore"
    }
}