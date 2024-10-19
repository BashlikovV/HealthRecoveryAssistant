package by.bashlikovvv.home.presentation.ui.component

import by.bashlikovvv.home.presentation.ui.store.HomeStore

interface HomeComponent {
    val store: HomeStore

    class Configuration(val harFileUri: String? = null)
}