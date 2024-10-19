package by.bashlikovvv.home.presentation.ui.store

import by.bashlikovvv.domain.model.WearableEvents
import by.bashlikovvv.home.presentation.ui.component.HomeComponent
import by.bashlikovvv.home.presentation.ui.store.HomeStore.*
import by.bashlikovvv.ui.base.BaseStoreFactory
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory

internal class HomeStoreFactory(
    storeFactory: StoreFactory,
    private val configuration: HomeComponent.Configuration,
) : BaseStoreFactory<HomeStore>(storeFactory) {
    override fun create(): HomeStore =
        object : HomeStore, Store<Intent, State, Nothing> by storeFactory.create(
            name = STORE_NAME,
            initialState = State(),
            autoInit = true,
            executorFactory = ::HomeStoreExecutor,
            reducer = reducerImpl,
            bootstrapper = configuration.harFileUri?.let {
                SimpleBootstrapper(Action.InitializeWithHARFile(it))
            },
        ) {}

    private val reducerImpl =
        Reducer<State, Msg> { msg ->
            when (msg) {
                is Msg.HRAFileData -> this.copy(fileContent = msg.data, fileName = msg.name)
            }
        }

    internal sealed class Msg {
        data class HRAFileData(
            val name: String,
            val data: WearableEvents?,
        ) : Msg()
    }

    internal sealed interface Action {
        data class InitializeWithHARFile(val uri: String) : Action
    }

    companion object {
        const val STORE_NAME = "HomeStore"
    }
}