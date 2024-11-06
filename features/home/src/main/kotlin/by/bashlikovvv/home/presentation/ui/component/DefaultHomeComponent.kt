package by.bashlikovvv.home.presentation.ui.component

import by.bashlikovvv.domain.model.BluetoothDevice
import by.bashlikovvv.home.presentation.ui.component.HomeComponent.*
import by.bashlikovvv.home.presentation.ui.store.HomeStore
import by.bashlikovvv.home.presentation.ui.store.HomeStoreFactory
import by.bashlikovvv.ui.base.BaseComponent
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory

class DefaultHomeComponent(
    componentContext: ComponentContext,
    configuration: Configuration,
    storeFactory: StoreFactory,
    private val onOutput: (Output) -> Unit,
) : HomeComponent, BaseComponent(componentContext) {
    override val store: HomeStore = instanceKeeper.getStore {
        HomeStoreFactory(storeFactory, configuration).create()
    }

    override fun startDiscoveringNewDevices() = onOutput(Output.StartDiscoveringNewDevices)

    override fun openDeviceSettings(device: BluetoothDevice) = onOutput(Output.OpenDeviceSettings(device))
}