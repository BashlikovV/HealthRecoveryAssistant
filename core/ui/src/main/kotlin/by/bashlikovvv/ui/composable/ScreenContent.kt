package by.bashlikovvv.ui.composable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.arkivanov.mvikotlin.core.store.Store

@Composable
fun <Intent : Any, State : Any, Label : Any> ScreenContent(
    contractProvider: Store<Intent, State, Label>,
    initialState: State,
    content: @Composable ScreenScope<Intent, State, Label>.(state: State, labels: Label?) -> Unit,
) {
    LaunchedEffect(key1 = Unit) { contractProvider.init() }
    val screenScope = rememberScreenScope(
        contractProvider = contractProvider,
        initialState = initialState
    )
    content.invoke(screenScope, screenScope.fetchState(), screenScope.fetchLabel())
}