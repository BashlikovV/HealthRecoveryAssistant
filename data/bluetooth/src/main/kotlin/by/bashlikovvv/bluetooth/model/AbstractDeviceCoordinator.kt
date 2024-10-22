package by.bashlikovvv.bluetooth.model

import java.util.regex.Pattern

abstract class AbstractDeviceCoordinator : DeviceCoordinator {
    private var supportedDeviceName: Pattern? = null

    protected fun getSupportedDeviceName(): Pattern? {
        return null
    }

    override fun supports(candidate: GBDeviceCandidate): Boolean {
        if (supportedDeviceName == null) {
            supportedDeviceName = getSupportedDeviceName()
        }
        if (supportedDeviceName == null) return false

        return supportedDeviceName?.matcher(candidate.deviceName ?: "")?.matches() == true
    }
}