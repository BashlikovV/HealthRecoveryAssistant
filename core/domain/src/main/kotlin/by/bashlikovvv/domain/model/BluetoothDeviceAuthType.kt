package by.bashlikovvv.domain.model

sealed class BluetoothDeviceAuthType {
    data object RequiresKey : BluetoothDeviceAuthType()

    data object Default : BluetoothDeviceAuthType()
}