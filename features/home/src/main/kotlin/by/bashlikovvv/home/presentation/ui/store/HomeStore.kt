package by.bashlikovvv.home.presentation.ui.store

import android.content.Context
import android.os.Parcelable
import by.bashlikovvv.home.presentation.ui.store.HomeStore.*
import com.arkivanov.mvikotlin.core.store.Store
import kotlinx.parcelize.Parcelize

interface HomeStore : Store<Intent, State, Label> {
    sealed class Intent {
        data class Vibrate(
            val duration: Long,
            val amplitude: Int,
        ) : Intent()

        data class Initialize(val context: Context) : Intent()

        data object Destroy : Intent()
    }

    @Parcelize
    data class State(
        val tmp: String = "",
    ) : Parcelable

    sealed class Label
}