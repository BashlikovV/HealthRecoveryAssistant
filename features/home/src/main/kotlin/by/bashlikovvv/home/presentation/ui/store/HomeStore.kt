package by.bashlikovvv.home.presentation.ui.store

import android.content.Context
import android.os.Parcelable
import androidx.activity.result.ActivityResult
import by.bashlikovvv.domain.model.WearableEvents
import by.bashlikovvv.home.presentation.ui.store.HomeStore.*
import com.arkivanov.mvikotlin.core.store.Store
import kotlinx.parcelize.Parcelize

interface HomeStore : Store<Intent, State, Nothing> {
    sealed class Intent {
        data class OnActivityResult(val activityResult: ActivityResult) : Intent()

        data class ScheduleFileData(
            val events: WearableEvents,
            val context: Context,
        ) : Intent()
    }

    @Parcelize
    data class State(
        val fileName: String? = null,
        val fileContent: WearableEvents? = null,
    ) : Parcelable
}