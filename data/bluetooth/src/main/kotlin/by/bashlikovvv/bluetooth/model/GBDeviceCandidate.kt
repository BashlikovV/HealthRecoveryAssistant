@file:SuppressLint("MissingPermission")
package by.bashlikovvv.bluetooth.model

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.os.Build
import android.os.Parcel
import android.os.ParcelUuid
import android.os.Parcelable
import android.os.Parcelable.Creator
import android.util.Log
import java.lang.reflect.InvocationTargetException
import java.util.UUID

class GBDeviceCandidate : Parcelable, Cloneable {
    val device: BluetoothDevice

    var rssi: Short
        private set

    var serviceUuids: Array<ParcelUuid>
        private set

    var deviceName: String? = null
        private set

    private var isBonded: Boolean? = null

    constructor(device: BluetoothDevice, rssi: Short, serviceUuids: Array<ParcelUuid>?) {
        this.device = device
        this.rssi = rssi
        this.serviceUuids = serviceUuids ?: emptyArray()
    }

    constructor(`in`: Parcel) {
        device = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            `in`.readParcelable(this::class.java.classLoader, BluetoothDevice::class.java)!!
        } else {
            `in`.readParcelable(this::class.java.classLoader)!!
        }
        rssi = `in`.readInt().toShort()
        serviceUuids = toParcelUuids(`in`.readParcelableArray(this::class.java.classLoader)!!)
        deviceName = `in`.readString()
        val isBondedInt = `in`.readInt()
        if (isBondedInt != -1) {
            isBonded = (isBondedInt == 1)
        }
    }

    override fun describeContents(): Int = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeParcelable(device, 0)
        dest.writeInt(rssi.toInt())
        dest.writeParcelableArray<ParcelUuid>(serviceUuids, 0)
        dest.writeString(deviceName)
        val tmp = isBonded?.let { isBondedNotNull -> if (isBondedNotNull) 1 else 0 } ?: -1
        dest.writeInt(tmp)
    }

    fun getMacAddress(): String = device.address

    fun addUuids(newUuids: Array<ParcelUuid>) {
        this.serviceUuids = mergeServiceUuids(serviceUuids, newUuids)
    }

    fun setRssi(rssi: Short) {
        this.rssi = rssi
    }

    fun isBonded(): Boolean {
        if (isBonded == null) {
            isBonded = try {
                device.bondState == BluetoothDevice.BOND_BONDED
            } catch (e: SecurityException) {
                Log.e("MYTAG", this::class.toString(), e)
                false
            }
        }

        return isBonded == true
    }

    fun refreshNameIfUnknown() {
        if (isNameKnown()) return

        try {
            val method = device.javaClass.getMethod("getAliasName")
            deviceName = method.invoke(device) as? String
        } catch (e: NoSuchMethodException) {
            Log.e("MYTAG", this::class.toString(), e)
        } catch (e: IllegalAccessException) {
            Log.e("MYTAG", this::class.toString(), e)
        } catch (e: InvocationTargetException) {
            Log.e("MYTAG", this::class.toString(), e)
        }
        if (deviceName == null || deviceName?.isEmpty() == true) {
            try {
                deviceName = device.name
            } catch (e: SecurityException) {
                Log.e("MYTAG", this::class.toString(), e)
            }
        }
    }

    fun isNameKnown(): Boolean = deviceName != null && deviceName?.isNotEmpty() == true

    private fun toParcelUuids(uuids: Array<Parcelable>): Array<ParcelUuid> {
        val ru = UUID.randomUUID()
        val uuids2 = Array<ParcelUuid>(uuids.size) { ParcelUuid(ru) }
        System.arraycopy(uuids, 0, uuids2, 0, uuids.size)
        return uuids2
    }

    private fun mergeServiceUuids(
        serviceUuids: Array<ParcelUuid>,
        deviceUuids: Array<ParcelUuid>
    ): Array<ParcelUuid> {
        val uuids = LinkedHashSet<ParcelUuid>()
        uuids.addAll(serviceUuids)
        uuids.addAll(deviceUuids)
        return uuids.toTypedArray()
    }

    companion object {
        @JvmField
        val CREATOR: Creator<GBDeviceCandidate> = object : Creator<GBDeviceCandidate> {
            override fun createFromParcel(source: Parcel): GBDeviceCandidate? {
                return GBDeviceCandidate(source)
            }

            override fun newArray(size: Int): Array<out GBDeviceCandidate?>? {
                return Array(size) { null }
            }
        }
    }
}