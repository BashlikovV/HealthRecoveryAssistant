package by.bashlikovvv.domain.model

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@[Parcelize Serializable SerialName("BluetoothDevice")]
data class BluetoothDevice(
    @SerialName("id") val id: Long,
    @SerialName("name") val name: String,
    @SerialName("address") val address: String,
    @SerialName("type") val type: BluetoothDeviceType,
) : Parcelable {
    companion object {
        @SuppressLint("MissingPermission")
        fun fromAndroidBluetoothDevice(
            device: BluetoothDevice,
            type: BluetoothDeviceType,
        ): by.bashlikovvv.domain.model.BluetoothDevice? {
            return try {
                BluetoothDevice(
                    id = device.hashCode().toLong(),
                    name = device.name,
                    address = device.address,
                    type = type,
                )
            } catch (_: SecurityException) {
                null
            }
        }
    }
}