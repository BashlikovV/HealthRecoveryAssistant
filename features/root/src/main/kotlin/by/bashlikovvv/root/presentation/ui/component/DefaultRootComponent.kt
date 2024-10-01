package by.bashlikovvv.root.presentation.ui.component

import by.bashlikovvv.home.presentation.ui.component.DefaultHomeComponent
import by.bashlikovvv.home.presentation.ui.component.HomeComponent
import by.bashlikovvv.root.presentation.ui.component.RootComponent.Child
import by.bashlikovvv.root.presentation.ui.store.RootStore
import by.bashlikovvv.root.presentation.ui.store.RootStoreFactory
import by.bashlikovvv.ui.base.BaseComponent
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.Value
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import kotlinx.serialization.Serializable

class DefaultRootComponent(
    componentContext: ComponentContext,
    private val storeFactory: StoreFactory,
) : RootComponent, BaseComponent(componentContext) {
    private val navigation = StackNavigation<Config>()

    override val stack: Value<ChildStack<*, Child>> =
        childStack(
            source = navigation,
            serializer = Config.serializer(),
            initialConfiguration = Config.Home,
            handleBackButton = true,
            childFactory = ::child
        )

    override val store: RootStore = instanceKeeper.getStore {
        RootStoreFactory(storeFactory).create()
    }

    private fun child(config: Config, childComponentContext: ComponentContext): Child =
        when(config) {
            Config.Home -> Child.Home(homeComponent(childComponentContext))
        }

    private fun homeComponent(componentContext: ComponentContext): HomeComponent =
        DefaultHomeComponent(
            componentContext = componentContext,
            storeFactory = storeFactory
        )

    @Serializable
    private sealed class Config {
        @Serializable
        data object Home : Config()
    }
}