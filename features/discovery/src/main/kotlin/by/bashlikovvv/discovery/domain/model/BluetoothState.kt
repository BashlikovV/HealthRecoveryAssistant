package by.bashlikovvv.discovery.domain.model

import android.os.Parcelable
import androidx.compose.runtime.Composable
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class BluetoothState : BluetoothActionCompose, Parcelable {
    object Unknown : BluetoothState() {
        override val text: String @Composable get() = "TODO"
    }

    @Parcelize
    object On : BluetoothState() {
        override val text: String @Composable get() = "TODO"
    }

    @Parcelize
    object Off : BluetoothState() {
        override val text: String @Composable get() = "TODO"
    }

    @Parcelize
    object TurningOn : BluetoothState() {
        override val text: String @Composable get() = "TODO"
    }

    @Parcelize
    object TurningOff : BluetoothState() {
        override val text: String @Composable get() = "TODO"
    }
}