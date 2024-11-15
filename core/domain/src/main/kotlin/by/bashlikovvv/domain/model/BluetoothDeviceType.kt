package by.bashlikovvv.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@[Parcelize Serializable SerialName("BluetoothDeviceType")]
sealed class BluetoothDeviceType(@SerialName("name") val name: String): Parcelable {
    @SerialName("requiresKey")
    val requiresKey: Boolean get() = getAuthType() == BluetoothDeviceAuthType.RequiresKey

    @[Parcelize Serializable]
    data object Unknown : BluetoothDeviceType("UNKNOWN") {
        override fun getAuthType(): BluetoothDeviceAuthType = BluetoothDeviceAuthType.Default
    }

    @[Parcelize Serializable]
    data object MiBand5 : BluetoothDeviceType("MI_BAND_5") {
        override fun getAuthType(): BluetoothDeviceAuthType = BluetoothDeviceAuthType.RequiresKey
    }

    protected open fun getAuthType(): BluetoothDeviceAuthType = BluetoothDeviceAuthType.Default
}