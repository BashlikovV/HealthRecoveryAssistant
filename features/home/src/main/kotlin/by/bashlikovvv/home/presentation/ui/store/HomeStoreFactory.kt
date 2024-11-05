package by.bashlikovvv.home.presentation.ui.store

import by.bashlikovvv.domain.model.WearableEvents
import by.bashlikovvv.home.domain.model.DevicesListItems
import by.bashlikovvv.home.presentation.ui.component.HomeComponent
import by.bashlikovvv.home.presentation.ui.store.HomeStore.*
import by.bashlikovvv.ui.base.BaseStoreFactory
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import kotlinx.collections.immutable.toPersistentList

internal class HomeStoreFactory(
    storeFactory: StoreFactory,
    private val configuration: HomeComponent.Configuration,
) : BaseStoreFactory<HomeStore>(storeFactory) {
    override fun create(): HomeStore = HomeStoreImpl()

    private inner class HomeStoreImpl :
        HomeStore, Store<Intent, State, Nothing> by storeFactory.create(
            name = STORE_NAME,
            initialState = State(),
            autoInit = true,
            executorFactory = ::HomeStoreExecutor,
            reducer = reducerImpl,
            bootstrapper = bootstrap(configuration),
        )

    private fun bootstrap(
        configuration: HomeComponent.Configuration
    ): SimpleBootstrapper<Action> {
        return SimpleBootstrapper(
            configuration.harFileUri?.let { Action.InitializeWithHARFile(it) } ?: Action.Initialize
        )
    }

    private val reducerImpl =
        Reducer<State, Msg> { msg ->
            when (msg) {
                is Msg.HRAFileData -> dispatch(msg)
                is Msg.Devices -> dispatch(msg)
                is Msg.DeviceConnected -> dispatch(msg)
            }
        }

    private fun State.dispatch(msg: Msg.HRAFileData): State {
        return this.copy(fileContent = msg.data, fileName = msg.name)
    }

    private fun State.dispatch(msg: Msg.Devices): State {
        return this.copy(devicesList = msg.devices.toPersistentList())
    }

    private fun State.dispatch(msg: Msg.DeviceConnected): State {
        return this.copy(
            devicesList = this.devicesList.map { device ->
                when(device) {
                    is DevicesListItems.Device -> if (device == msg.device) {
                        device.copy(connected = true)
                    } else {
                        device
                    }
                }
            }.toPersistentList()
        )
    }

    internal sealed class Msg {
        data class HRAFileData(
            val name: String,
            val data: WearableEvents?,
        ) : Msg()

        data class Devices(
            val devices: List<DevicesListItems.Device>
        ) : Msg()

        data class DeviceConnected(
            val device: DevicesListItems.Device
        ) : Msg()
    }

    internal sealed interface Action {
        data object Initialize : Action

        data class InitializeWithHARFile(val uri: String) : Action
    }

    companion object {
        const val STORE_NAME = "HomeStore"
    }
}