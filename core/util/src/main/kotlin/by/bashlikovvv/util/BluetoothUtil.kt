package by.bashlikovvv.util

import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.os.Build

/**
 * @return [BluetoothDevice] received from broadcast intent
 * */
fun Intent.getBluetoothDeviceFromIntent(): BluetoothDevice? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getParcelableExtra(BluetoothDevice.EXTRA_DEVICE, BluetoothDevice::class.java)
    } else {
        getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
    }
}

