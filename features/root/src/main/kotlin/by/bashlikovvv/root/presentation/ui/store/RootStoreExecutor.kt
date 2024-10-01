package by.bashlikovvv.root.presentation.ui.store

import by.bashlikovvv.root.presentation.ui.store.RootStore.Intent
import by.bashlikovvv.root.presentation.ui.store.RootStore.Label
import by.bashlikovvv.root.presentation.ui.store.RootStore.State
import by.bashlikovvv.root.presentation.ui.store.RootStoreFactory.Action
import by.bashlikovvv.root.presentation.ui.store.RootStoreFactory.Msg
import by.bashlikovvv.ui.base.BaseCoroutineExecutor

class RootStoreExecutor : BaseCoroutineExecutor<Intent, Action, State, Msg, Label>() {
    override fun executeIntent(intent: Intent, getState: () -> State) {
        when (intent) {
            else -> { }
        }
    }

    override fun executeAction(action: Action, getState: () -> State) {
        when (action) {
            Action.Init -> initialize()
        }
    }

    private fun initialize() {
        launchIO(
            safeAction = { },
            onError = { _: Throwable -> }
        )
    }
}