package by.bashlikovvv.home.presentation.ui.store

import by.bashlikovvv.home.presentation.ui.store.HomeStore.*
import by.bashlikovvv.home.presentation.ui.store.HomeStoreFactory.*
import by.bashlikovvv.ui.base.BaseCoroutineExecutor
import by.bashlikovvv.home.data.remote.HomeRepository
import by.bashlikovvv.domain.model.WearableEvent
import by.bashlikovvv.domain.model.VibrationDescriptor
import by.bashlikovvv.domain.model.VibrationAction
import org.koin.core.component.inject

class HomeStoreExecutor : BaseCoroutineExecutor<Intent, Nothing, State, Msg, Label>() {
    private val homeRepository: HomeRepository by inject()

    override fun executeIntent(intent: Intent, getState: () -> State) {
        when (intent) {
            is Intent.Vibrate -> simpleVibrate(intent)
            is Intent.Initialize -> homeRepository.initialize(intent.context)
            is Intent.Destroy -> homeRepository.destroy()
        }
    }

    private fun simpleVibrate(intent: Intent.Vibrate) {
        homeRepository.dispatchEvent(testVibration)
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