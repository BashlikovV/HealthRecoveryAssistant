package by.bashlikovvv.bluetooth.coordinator

import by.bashlikovvv.bluetooth.model.AbstractDeviceCoordinator

class UnknownDeviceCoordinator : AbstractDeviceCoordinator() {
    override val orderPriority: Int = 0
}