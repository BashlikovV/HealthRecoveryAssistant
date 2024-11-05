package by.bashlikovvv.discovery.presentation.ui.store

import android.Manifest
import android.os.Build
import by.bashlikovvv.discovery.domain.model.BluetoothState
import by.bashlikovvv.discovery.domain.model.DiscoveryListItems.Device
import by.bashlikovvv.discovery.presentation.ui.store.DiscoveryStore.*
import by.bashlikovvv.ui.base.BaseStoreFactory
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import kotlinx.collections.immutable.toPersistentList

class DiscoveryStoreFactory(
    storeFactory: StoreFactory,
) : BaseStoreFactory<DiscoveryStore>(storeFactory) {
    private val requiredPermissions = mutableListOf<String>()
        .apply {
            add(Manifest.permission.ACCESS_COARSE_LOCATION)
            add(Manifest.permission.ACCESS_FINE_LOCATION)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            }
            add(Manifest.permission.BLUETOOTH)
            add(Manifest.permission.BLUETOOTH_ADMIN)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                add(Manifest.permission.BLUETOOTH_SCAN)
                add(Manifest.permission.BLUETOOTH_CONNECT)
            }
        }

    override fun create(): DiscoveryStore = DiscoveryStoreImpl()

    private inner class DiscoveryStoreImpl :
        DiscoveryStore, Store<Intent, State, Label> by storeFactory.create(
            name = STORE_NAME,
            initialState = State(),
            bootstrapper = bootstrapper(),
            autoInit = true,
            executorFactory = ::DiscoveryStoreExecutor,
            reducer = reducerImpl,
        )

    private fun bootstrapper(): SimpleBootstrapper<Action.Initialize> {
        return SimpleBootstrapper(Action.Initialize(requiredPermissions))
    }

    private val reducerImpl = Reducer<State, Msg> { msg ->
        when(msg) {
            is Msg.Discover -> reduce(msg)
            is Msg.ChangeBluetoothState -> reduce(msg)
            is Msg.AddDevice -> reduce(msg)
            is Msg.DeviceBonding -> reduce(msg)
            is Msg.DeviceBonded -> reduce(msg)
            is Msg.DeviceError -> reduce(msg)
        }
    }

    private fun State.reduce(msg: Msg.Discover): State {
        return this.copy(isScanning = msg.isScanning)
    }

    private fun State.reduce(msg: Msg.ChangeBluetoothState): State {
        return this.copy(bluetoothState = msg.state)
    }

    private fun State.reduce(msg: Msg.AddDevice): State {
        return this.copy(
            devices = if (this.devices.any { it.id == msg.device.id }) {
                this.devices.map {
                    if (it.id == msg.device.id) {
                        it.copy(
                            name = msg.device.name,
                            address = msg.device.address,
                        )
                    } else {
                        it
                    }
                }
            } else {
                (this.devices + msg.device)
                    .toSet()
                    .toList()
            }.toPersistentList()
        )
    }

    private fun State.reduce(msg: Msg.DeviceBonding): State {
        return this.copy(
            devices = this.devices.map {
                if (it.address == msg.address) {
                    it.copy(isInProgress = true)
                } else {
                    it
                }
            }.toPersistentList()
        )
    }

    private fun State.reduce(msg: Msg.DeviceBonded): State {
        return this.copy(
            devices = this.devices.map {
                if (it.address == msg.address) {
                    it.copy(
                        isInProgress = false,
                        isBonded = true,
                        isError = false,
                    )
                } else {
                    it
                }
            }.toPersistentList()
        )
    }

    private fun State.reduce(msg: Msg.DeviceError): State {
        return this.copy(
            devices = this.devices.map {
                if (it.address == msg.address) {
                    it.copy(
                        isInProgress = false,
                        isError = true,
                    )
                } else {
                    it
                }
            }.toPersistentList()
        )
    }

    internal sealed class Msg {
        data class Discover(val isScanning: Boolean) : Msg()

        data class ChangeBluetoothState(val state: BluetoothState) : Msg()

        data class AddDevice(val device: Device) : Msg()

        data class DeviceBonding(val address: String) : Msg()

        data class DeviceBonded(val address: String) : Msg()

        data class DeviceError(val address: String) : Msg()
    }

    internal sealed interface Action {
        data class Initialize(val permissions: List<String>) : Action
    }

    companion object {
        const val STORE_NAME = "DiscoveryStore"
    }
}