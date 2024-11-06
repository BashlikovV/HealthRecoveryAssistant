package by.bashlikovvv.home.domain.model

import android.os.Parcelable
import by.bashlikovvv.domain.model.BluetoothDevice
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class DevicesListItems(open val id: Int) : Parcelable {
    @Parcelize
    data class Device(
        val device: BluetoothDevice,
        val connected: Boolean,
    ) : DevicesListItems(device.id.toInt()) {
        val name: String get() = device.name

        val address: String get() = device.address
    }
}