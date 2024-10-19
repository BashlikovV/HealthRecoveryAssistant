package by.bashlikovvv.home.presentation.ui.store

import android.app.Activity.RESULT_OK
import android.content.Context
import android.net.Uri
import androidx.activity.result.ActivityResult
import by.bashlikovvv.common.repository.HARFilesRepository
import by.bashlikovvv.common.repository.WearableRepository
import by.bashlikovvv.domain.base.BaseResult
import by.bashlikovvv.domain.model.WearableEvents
import by.bashlikovvv.home.presentation.ui.store.HomeStore.*
import by.bashlikovvv.home.presentation.ui.store.HomeStoreFactory.*
import by.bashlikovvv.ui.base.BaseCoroutineExecutor
import org.koin.core.component.inject

internal class HomeStoreExecutor : BaseCoroutineExecutor<Intent, Action, State, Msg, Nothing>() {
    private val harFilesRepository: HARFilesRepository by inject()

    private val wearableRepository: WearableRepository by inject()

    override fun executeIntent(intent: Intent, getState: () -> State) {
        when (intent) {
            is Intent.OnActivityResult -> onActivityResultIntent(intent.activityResult)
            is Intent.ScheduleFileData -> onScheduleFileDataIntent(intent.events, intent.context)
        }
    }

    override fun executeAction(action: Action, getState: () -> State) {
        when(action) {
            is Action.InitializeWithHARFile -> openHARFile(Uri.parse(action.uri))
        }
    }

    private fun onActivityResultIntent(result: ActivityResult) {
        if (result.resultCode == RESULT_OK) {
            result.data?.data?.let { openHARFile(it) }
        }
    }

    private fun openHARFile(uri: Uri) = launchIO(
        safeAction = {
            when(val rResult = harFilesRepository.openHRAFile(uri)) {
                is BaseResult.Success -> dispatchOnMainThread(
                    Msg.HRAFileData(
                        name = uri.lastPathSegment ?: "null",
                        data = rResult.data
                    )
                )
                is BaseResult.Failure -> Unit
            }
        }
    )

    private fun onScheduleFileDataIntent(
        events: WearableEvents,
        context: Context,
    ) {
        launchIO(
            safeAction = { wearableRepository.scheduleHRAFileData(context, events) },
        )
    }
}