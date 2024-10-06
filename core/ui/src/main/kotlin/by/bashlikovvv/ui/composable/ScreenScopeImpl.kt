package by.bashlikovvv.ui.composable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.runtime.saveable.rememberSaveable
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class ScreenScopeImpl<Intent : Any, State : Any, Label : Any>(
    private val contractProvider: Store<Intent, State, Label>,
    private val initialState: State,
) : ScreenScope<Intent, State, Label> {
    override fun dispatchIntent(intent: Intent) {
        contractProvider.accept(intent)
    }

    @[Composable OptIn(ExperimentalCoroutinesApi::class)]
    override fun fetchState(): State {
        val state by contractProvider.stateFlow.collectAsState(
            initial = initialState
        )
        return state
    }

    @Composable
    override fun fetchLabel(): Label? {
        val labels by contractProvider.labels.collectAsState(null)
        return labels
    }

    override fun getState(): State = contractProvider.state
}

@Composable
fun <Intent : Any, State : Any, Label : Any> rememberScreenScope(
    contractProvider: Store<Intent, State, Label>,
    initialState: State,
): ScreenScope<Intent, State, Label> {
    return rememberSaveable(saver = screenScopeSaver(contractProvider)) {
        ScreenScopeImpl(contractProvider, initialState)
    }
}

private fun <Intent : Any, State : Any, Label : Any> screenScopeSaver(
    contractProvider: Store<Intent, State, Label>,
): Saver<ScreenScopeImpl<Intent, State, Label>, State> = object : Saver<ScreenScopeImpl<Intent, State, Label>, State> {
    override fun SaverScope.save(value: ScreenScopeImpl<Intent, State, Label>): State {
        return value.getState()
    }

    override fun restore(value: State): ScreenScopeImpl<Intent, State, Label> {
        return ScreenScopeImpl(contractProvider, value)
    }
}