package by.bashlikovvv.root.presentation.ui.store

import android.os.Parcelable
import by.bashlikovvv.root.presentation.ui.store.RootStore.Intent
import by.bashlikovvv.root.presentation.ui.store.RootStore.Label
import by.bashlikovvv.root.presentation.ui.store.RootStore.State
import by.bashlikovvv.ui.res.language.LanguageUiType
import com.arkivanov.mvikotlin.core.store.Store
import kotlinx.parcelize.Parcelize

interface RootStore : Store<Intent, State, Label> {
    sealed class Intent

    @Parcelize
    data class State(
        val languageUiType: LanguageUiType = LanguageUiType.EN,
    ) : Parcelable

    sealed class Label
}