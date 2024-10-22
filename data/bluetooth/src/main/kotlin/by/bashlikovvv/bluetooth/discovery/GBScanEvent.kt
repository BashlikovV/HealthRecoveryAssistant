package by.bashlikovvv.bluetooth.discovery

import android.bluetooth.BluetoothDevice
import android.os.ParcelUuid

class GBScanEvent(
    val device: BluetoothDevice,
    val rssi: Short,
    val serviceUuids: Array<ParcelUuid>,
)