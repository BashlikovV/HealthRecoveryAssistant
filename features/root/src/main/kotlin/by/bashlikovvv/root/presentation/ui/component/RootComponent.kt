package by.bashlikovvv.root.presentation.ui.component

import by.bashlikovvv.home.presentation.ui.component.HomeComponent
import by.bashlikovvv.root.presentation.ui.store.RootStore
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value

interface RootComponent {
    val stack: Value<ChildStack<*, Child>>

    val store: RootStore

    fun dispatchIntent(intent: RootStore.Intent)

    sealed class Child {
        data class Home(val component: HomeComponent) : Child()
    }
}