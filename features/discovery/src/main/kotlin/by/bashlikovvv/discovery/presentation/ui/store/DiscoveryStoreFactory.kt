package by.bashlikovvv.discovery.presentation.ui.store

import by.bashlikovvv.discovery.domain.model.BluetoothState
import by.bashlikovvv.discovery.domain.model.DiscoveryListItems.Device
import by.bashlikovvv.discovery.presentation.ui.store.DiscoveryStore.*
import by.bashlikovvv.ui.base.BaseStoreFactory
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory

class DiscoveryStoreFactory(
    storeFactory: StoreFactory,
) : BaseStoreFactory<DiscoveryStore>(storeFactory) {
    override fun create(): DiscoveryStore =
        object : DiscoveryStore, Store<Intent, State, Label> by storeFactory.create(
            name = STORE_NAME,
            initialState = State(),
            autoInit = true,
            executorFactory = ::DiscoveryStoreExecutor,
            reducer = reducerImpl,
        ) {}

    private val reducerImpl = Reducer<State, Msg> { msg ->
        when(msg) {
            is Msg.Discover -> this.copy(isScanning = msg.isScanning)
            is Msg.ChangeBluetoothState -> this.copy(bluetoothState = msg.state)
            is Msg.AddDevice -> this.copy(
                devices = (this.devices + msg.device).toSet().toList()
            )
        }
    }

    internal sealed class Msg {
        data class Discover(val isScanning: Boolean) : Msg()

        data class ChangeBluetoothState(val state: BluetoothState) : Msg()

        data class AddDevice(val device: Device) : Msg()
    }

    internal sealed interface Action

    companion object {
        const val STORE_NAME = "DiscoveryStore"
    }
}