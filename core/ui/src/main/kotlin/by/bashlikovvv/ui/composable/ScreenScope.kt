package by.bashlikovvv.ui.composable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable

@Immutable
interface ScreenScope<Intent : Any, State : Any, Label: Any> {
    fun dispatchIntent(intent: Intent)

    @Composable
    fun fetchState(): State

    @Composable
    fun fetchLabel(): Label?

    fun getState(): State
}