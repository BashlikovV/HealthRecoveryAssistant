package by.bashlikovvv.home.presentation.ui.store

import by.bashlikovvv.home.presentation.ui.store.HomeStore.*
import by.bashlikovvv.home.presentation.ui.store.HomeStoreFactory.*
import by.bashlikovvv.ui.base.BaseCoroutineExecutor
import by.bashlikovvv.home.data.remote.RootRepository
import by.bashlikovvv.domain.model.WearableEvent
import by.bashlikovvv.domain.model.VibrationDescriptor
import by.bashlikovvv.domain.model.VibrationAction
import org.koin.core.component.inject

class HomeStoreExecutor : BaseCoroutineExecutor<Intent, Action, State, Msg, Label>() {
    private val rootRepository: RootRepository by inject()

    override fun executeIntent(intent: Intent, getState: () -> State) {
        when (intent) {
            is Intent.Vibrate -> simpleVibrate(intent)
        }
    }

    private fun simpleVibrate(intent: Intent.Vibrate) {
        rootRepository.dispatchEvent(testVibration)
    }

    private val testVibration = WearableEvent(
        vibrationDescriptor = VibrationDescriptor(
            actions = listOf(
                VibrationAction(
                    duration = 100,
                    amplitude = 255U,
                ),
                VibrationAction(
                    duration = 100,
                    amplitude = 255U,
                ),
                VibrationAction(
                    duration = 100,
                    amplitude = 255U,
                ),
                VibrationAction(
                    duration = 1000,
                    amplitude = 255U,
                ),
                VibrationAction(
                    duration = 1000,
                    amplitude = 255U,
                ),
                VibrationAction(
                    duration = 1000,
                    amplitude = 255U,
                ),
                VibrationAction(
                    duration = 100,
                    amplitude = 255U,
                ),
                VibrationAction(
                    duration = 100,
                    amplitude = 255U,
                ),
                VibrationAction(
                    duration = 100,
                    amplitude = 255U,
                ),
            )
        )
    )
}