package by.bashlikovvv.discovery.presentation.ui.store

import android.os.Parcelable
import by.bashlikovvv.discovery.domain.model.BluetoothAction
import by.bashlikovvv.discovery.domain.model.BluetoothState
import by.bashlikovvv.discovery.domain.model.BondAction
import by.bashlikovvv.discovery.domain.model.DiscoveryListItems.Device
import by.bashlikovvv.discovery.presentation.ui.store.DiscoveryStore.*
import com.arkivanov.mvikotlin.core.store.Store
import kotlinx.parcelize.Parcelize

interface DiscoveryStore : Store<Intent, State, Label> {
    sealed class Intent {
        data object DiscoveryButtonClicked : Intent()

        data class OnBluetoothAction(val action: BluetoothAction) : Intent()

        data object StartDiscovery : Intent()

        data object CancelDiscovery : Intent()

        data class BondDevice(val address: String) : Intent()

        data class OnBondAction(val action: BondAction) : Intent()

        data object Vibrate : Intent()
    }

    @Parcelize
    data class State(
        val isScanning: Boolean = false,
        val bluetoothState: BluetoothState = BluetoothState.Off,
        val devices: List<Device> = emptyList(),
    ) : Parcelable

    sealed class Label {
        data object TurnOnBluetooth : Label()
    }
}