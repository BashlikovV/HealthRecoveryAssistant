package by.bashlikovvv.home.presentation.ui.component

import by.bashlikovvv.home.presentation.ui.store.HomeStore

interface HomeComponent {
    val store: HomeStore

    fun startDiscoveringNewDevices()

    class Configuration(val harFileUri: String? = null)

    sealed interface Output {
        data object StartDiscoveringNewDevices : Output
    }
}