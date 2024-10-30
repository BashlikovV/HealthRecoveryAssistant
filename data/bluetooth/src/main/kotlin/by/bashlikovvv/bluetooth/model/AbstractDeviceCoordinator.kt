package by.bashlikovvv.bluetooth.model

import android.bluetooth.le.ScanFilter
import java.util.regex.Pattern

abstract class AbstractDeviceCoordinator : DeviceCoordinator {
    private var supportedDeviceName: Pattern? = null

    override val connectionType: DeviceCoordinator.ConnectionType
        get() = DeviceCoordinator.ConnectionType.BOTH

    protected open fun getSupportedDeviceName(): Pattern? {
        return null
    }

    override fun supports(candidate: GBDeviceCandidate): Boolean {
        if (supportedDeviceName == null) {
            supportedDeviceName = getSupportedDeviceName()
        }
        if (supportedDeviceName == null) return false

        return supportedDeviceName?.matcher(candidate.deviceName ?: "(unknown)")?.matches() == true
    }

    override fun isConnectable(): Boolean = true

    override fun createBLEScanFilters(): List<ScanFilter> = emptyList()

    override fun createDevice(candidate: GBDeviceCandidate, deviceType: DeviceType): GBDevice {
        return GBDevice(
            device = candidate.device,
            deviceType = deviceType,
        )
    }

    companion object {
        const val BASE_UUID = "0000%s-0000-1000-8000-00805f9b34fb"
    }
}