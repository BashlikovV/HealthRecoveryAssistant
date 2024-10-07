package by.bashlikovvv.home.presentation.ui.store

import android.app.Activity.RESULT_OK
import android.content.Context
import androidx.activity.result.ActivityResult
import by.bashlikovvv.domain.base.BaseResult
import by.bashlikovvv.domain.model.WearableEvents
import by.bashlikovvv.home.presentation.ui.store.HomeStore.*
import by.bashlikovvv.home.presentation.ui.store.HomeStoreFactory.*
import by.bashlikovvv.home.repository.HomeRepository
import by.bashlikovvv.ui.base.BaseCoroutineExecutor
import org.koin.core.component.inject

class HomeStoreExecutor : BaseCoroutineExecutor<Intent, Nothing, State, Msg, Label>() {
    private val homeRepository: HomeRepository by inject()

    override fun executeIntent(intent: Intent, getState: () -> State) {
        when (intent) {
            is Intent.OnActivityResult -> onActivityResultIntent(intent.activityResult)
            is Intent.ScheduleFileData -> onScheduleFileDataIntent(intent.events, intent.context)
        }
    }

    private fun onActivityResultIntent(result: ActivityResult) {
        if (result.resultCode == RESULT_OK) {
            result.data?.data?.let {
                launchIO(
                    safeAction = {
                        when(val rResult = homeRepository.openHRAFile(it)) {
                            is BaseResult.Success -> dispatchOnMainThread(
                                Msg.HRAFileData(
                                    name = result.data?.data?.lastPathSegment ?: "null",
                                    data = rResult.data
                                )
                            )
                            is BaseResult.Failure -> Unit
                        }
                    }
                )
            }
        }
    }

    private fun onScheduleFileDataIntent(
        events: WearableEvents,
        context: Context,
    ) {
        launchIO(
            safeAction = { homeRepository.scheduleHRAFileData(context, events) }
        )
    }
}