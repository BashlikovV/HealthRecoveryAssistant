package by.bashlikovvv.root.presentation.ui.store

import android.Manifest
import android.os.Build
import by.bashlikovvv.root.presentation.ui.store.RootStore.Intent
import by.bashlikovvv.root.presentation.ui.store.RootStore.Label
import by.bashlikovvv.root.presentation.ui.store.RootStore.State
import by.bashlikovvv.ui.base.BaseStoreFactory
import com.arkivanov.mvikotlin.core.store.Bootstrapper
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory

class RootStoreFactory(storeFactory: StoreFactory) : BaseStoreFactory<RootStore>(storeFactory) {
    private val requiredPermissions = mutableListOf<String>().apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            add(Manifest.permission.FOREGROUND_SERVICE)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            add(Manifest.permission.POST_NOTIFICATIONS)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            add(Manifest.permission.FOREGROUND_SERVICE_CONNECTED_DEVICE)
        }
    }

    override fun create(): RootStore = RootStoreImpl()

    private inner class RootStoreImpl :
        RootStore, Store<Intent, State, Label> by storeFactory.create(
            name = STORE_NAME,
            initialState = State(),
            bootstrapper = bootstrapper(requiredPermissions),
            executorFactory = ::RootStoreExecutor,
            reducer = reducerImpl,
        )

    sealed class Msg {
        data class Error(val th: Throwable) : Msg()
    }

    private val reducerImpl =
        Reducer<State, Msg> { msg ->
            when (msg) {
                else -> this
            }
        }

    private fun bootstrapper(permissions: List<String>): Bootstrapper<Action> {
        return SimpleBootstrapper(Action.Initialize(permissions))
    }

    internal sealed interface Action {
        data class Initialize(val permissions: List<String>) : Action
    }

    companion object {
        private const val STORE_NAME = "RootStore"
    }
}