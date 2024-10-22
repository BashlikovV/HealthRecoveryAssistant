package by.bashlikovvv.discovery.presentation.ui.store

import android.os.Parcelable
import by.bashlikovvv.discovery.domain.model.BluetoothAction
import by.bashlikovvv.discovery.domain.model.BluetoothState
import by.bashlikovvv.discovery.domain.model.DiscoveryListItems.Device
import by.bashlikovvv.discovery.presentation.ui.store.DiscoveryStore.*
import com.arkivanov.mvikotlin.core.store.Store
import kotlinx.parcelize.Parcelize

interface DiscoveryStore : Store<Intent, State, Label> {
    sealed class Intent {
        data object DiscoveryButtonClicked : Intent()

        data class ConnectDevice(val candidate: String) : Intent()

        data class ShowDialog(
            val title: String = "",
            val text: String = "Ok",
            val confirmButton: String? = null,
            val dismissButton: String? = null,
        ) : Intent()

        data class OnBluetoothAction(val action: BluetoothAction) : Intent()
    }

    @Parcelize
    data class State(
        val isScanning: Boolean = false,
        val bluetoothState: BluetoothState = BluetoothState.Off,
        val devices: List<Device> = emptyList(),
    ) : Parcelable

    sealed class Label {
        data class ShowDialog(
            val title: String = "",
            val text: String = "",
            val confirmButton: String? = null,
            val dismissButton: String? = null,
        ) : Label()
    }
}