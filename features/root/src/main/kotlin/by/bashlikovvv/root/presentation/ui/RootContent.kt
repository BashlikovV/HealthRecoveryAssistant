package by.bashlikovvv.root.presentation.ui

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import by.bashlikovvv.home.presentation.ui.HomeContent
import by.bashlikovvv.root.presentation.ui.component.RootComponent
import by.bashlikovvv.root.presentation.ui.store.RootStore
import by.bashlikovvv.ui.composable.ScreenContent
import by.bashlikovvv.ui.theme.HealthRecoveryAssistantTheme
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation

@Composable
fun RootContent(
    component: RootComponent,
    modifier: Modifier = Modifier,
) {
    ScreenContent(
        contractProvider = component.store,
        initialState = RootStore.State()
    ) { state, label ->
        HealthRecoveryAssistantTheme(
            languageUiType = state.languageUiType
        ) {
            Surface(
                modifier = modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.systemBars),
            ) {
                Children(
                    stack = component.stack,
                    modifier = Modifier.fillMaxSize(),
                    animation = stackAnimation()
                ) {
                    when(val instance = it.instance) {
                        is RootComponent.Child.Home -> HomeContent(instance.component)
                    }
                }
            }
        }
    }
}