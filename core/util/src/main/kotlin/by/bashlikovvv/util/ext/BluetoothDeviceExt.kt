package by.bashlikovvv.util.ext

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice

val BluetoothDevice.deviceName: String?
    @SuppressLint("MissingPermission")
    get() {
        return try {
            name ?: run {
                val method = this::class.java.getMethod("getAliasName")
                method.invoke(this) as String
            }
        } catch (_: NoSuchMethodException) { null }
    }