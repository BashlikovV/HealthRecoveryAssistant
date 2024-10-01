package by.bashlikovvv.home.presentation.ui.component

import by.bashlikovvv.home.presentation.ui.store.HomeStore
import by.bashlikovvv.home.presentation.ui.store.HomeStoreFactory
import by.bashlikovvv.ui.base.BaseComponent
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory

class DefaultHomeComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
) : HomeComponent, BaseComponent(componentContext) {
    override val store: HomeStore = instanceKeeper.getStore {
        HomeStoreFactory(storeFactory).create()
    }
}