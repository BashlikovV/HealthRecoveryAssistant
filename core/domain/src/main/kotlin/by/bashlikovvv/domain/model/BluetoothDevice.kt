package by.bashlikovvv.domain.model

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@[Parcelize Serializable]
data class BluetoothDevice(
    val id: Long,
    val name: String,
    val address: String,
    val type: BluetoothDeviceType,
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