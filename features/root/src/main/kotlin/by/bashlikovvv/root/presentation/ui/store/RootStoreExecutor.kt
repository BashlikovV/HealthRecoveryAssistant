package by.bashlikovvv.root.presentation.ui.store

import android.content.Context
import by.bashlikovvv.common.repository.WearableRepository
import by.bashlikovvv.root.presentation.ui.store.RootStore.Intent
import by.bashlikovvv.root.presentation.ui.store.RootStore.Label
import by.bashlikovvv.root.presentation.ui.store.RootStore.State
import by.bashlikovvv.root.presentation.ui.store.RootStoreFactory.Msg
import by.bashlikovvv.ui.base.BaseCoroutineExecutor
import org.koin.core.component.inject

class RootStoreExecutor : BaseCoroutineExecutor<Intent, Nothing, State, Msg, Label>() {
    private val wearableRepository: WearableRepository by inject()

    override fun executeIntent(intent: Intent, getState: () -> State) {
        when (intent) {
            is Intent.Initialize -> initialize(intent.context)
            is Intent.Destroy -> destroy()
            is Intent.OnNewIntent -> onNewIntent(intent.intent)
        }
    }

    private fun initialize(context: Context) = launchIO(
        safeAction = { wearableRepository.initialize(context) },
        onError = { th: Throwable -> dispatch(Msg.Error(th)) }
    )

    private fun onNewIntent(intent: android.content.Intent) {
        if (intent.action == android.content.Intent.ACTION_VIEW) {
            intent.data?.let {  }
        }
    }

    private fun destroy() {
        wearableRepository.destroy()
    }
}