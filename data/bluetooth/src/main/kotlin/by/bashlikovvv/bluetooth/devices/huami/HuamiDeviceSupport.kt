package by.bashlikovvv.bluetooth.devices.huami

import by.bashlikovvv.bluetooth.model.DeviceSupport
import by.bashlikovvv.bluetooth.service.BtLEQueue

abstract class HuamiDeviceSupport : DeviceSupport {
    private var queue: BtLEQueue? = null

    companion object {
        const val MTU = 23
    }
}