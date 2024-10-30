package by.bashlikovvv.ui.dialog.store

import android.os.Parcelable
import by.bashlikovvv.ui.dialog.store.AlertDialogStore.*
import com.arkivanov.mvikotlin.core.store.Store
import kotlinx.parcelize.Parcelize

interface AlertDialogStore : Store<Intent, State, Nothing> {
    sealed class Intent {
        data object ShowDialog : Intent()

        object HideDialog : Intent()
    }

    @Parcelize
    data class State(
        val isVisible: Boolean = false,
    ) : Parcelable
}