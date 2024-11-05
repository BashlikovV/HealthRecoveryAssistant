package by.bashlikovvv.root.presentation.ui.component

import by.bashlikovvv.discovery.presentation.ui.component.DefaultDiscoveryComponent
import by.bashlikovvv.discovery.presentation.ui.component.DiscoveryComponent
import by.bashlikovvv.home.presentation.ui.component.DefaultHomeComponent
import by.bashlikovvv.home.presentation.ui.component.HomeComponent
import by.bashlikovvv.root.presentation.ui.component.RootComponent.Child
import by.bashlikovvv.root.presentation.ui.component.RootComponent.Child.*
import by.bashlikovvv.root.presentation.ui.store.RootStore
import by.bashlikovvv.root.presentation.ui.store.RootStoreFactory
import by.bashlikovvv.ui.base.BaseComponent
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.navigate
import com.arkivanov.decompose.router.stack.pushToFront
import com.arkivanov.decompose.value.Value
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.labels
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
            initialConfiguration = Config.Home(),
            handleBackButton = true,
            childFactory = ::child
        )

    override val store: RootStore = instanceKeeper.getStore {
        RootStoreFactory(storeFactory).create()
    }

    override fun dispatchIntent(intent: RootStore.Intent) {
        store.accept(intent)
    }

    init {
        observeLabels(store.labels) { label ->
            when(label) {
                is RootStore.Label.OpenHARFile -> onOpenHARFileLabel(label)
            }
        }
    }

    private fun child(config: Config, childComponentContext: ComponentContext): Child =
        when(config) {
            is Config.Home -> Home(homeComponent(childComponentContext, config.harFileUri))
            is Config.Discovery -> Discovery(discoveryComponent(childComponentContext))
        }

    private fun homeComponent(
        componentContext: ComponentContext,
        uri: String?,
    ): HomeComponent =
        DefaultHomeComponent(
            componentContext = componentContext,
            configuration = HomeComponent.Configuration(uri),
            storeFactory = storeFactory,
            onOutput = { output ->
                when(output) {
                    is HomeComponent.Output.StartDiscoveringNewDevices ->
                        navigation.pushToFront(Config.Discovery)
                }
            }
        )

    private fun discoveryComponent(componentContext: ComponentContext): DiscoveryComponent =
        DefaultDiscoveryComponent(
            componentContext = componentContext,
            storeFactory = storeFactory,
        )

    private fun onOpenHARFileLabel(label: RootStore.Label.OpenHARFile) {
        navigation.navigate { list ->
            list.map { item ->
                when (item) {
                    is Config.Home -> item.copy(harFileUri = label.uri.toString())
                    else -> item
                }
            }
        }
    }

    @Serializable
    private sealed class Config {
        @Serializable
        data class Home(val harFileUri: String? = null) : Config()

        @Serializable
        data object Discovery : Config()
    }
}