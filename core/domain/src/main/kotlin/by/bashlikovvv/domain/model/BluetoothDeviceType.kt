package by.bashlikovvv.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@[Parcelize Serializable]
sealed class BluetoothDeviceType(val name: String): Parcelable {
    val requiresKey: Boolean get() = getAuthType() == BluetoothDeviceAuthType.RequiresKey

    @[Parcelize Serializable]
    data object MiBand5 : BluetoothDeviceType("MI_BAND_5") {
        override fun getAuthType(): BluetoothDeviceAuthType = BluetoothDeviceAuthType.RequiresKey
    }

    protected open fun getAuthType(): BluetoothDeviceAuthType = BluetoothDeviceAuthType.Default
}