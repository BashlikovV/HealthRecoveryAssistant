package by.bashlikovvv.home.presentation.ui.component

import by.bashlikovvv.domain.model.BluetoothDevice
import by.bashlikovvv.home.presentation.ui.store.HomeStore

interface HomeComponent {
    val store: HomeStore

    fun startDiscoveringNewDevices()

    fun openDeviceSettings(device: BluetoothDevice)

    class Configuration(val harFileUri: String? = null)

    sealed interface Output {
        data object StartDiscoveringNewDevices : Output

        data class OpenDeviceSettings(val device: BluetoothDevice) : Output
    }
}