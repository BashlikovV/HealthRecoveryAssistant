package by.bashlikovvv.bluetooth.model

import by.bashlikovvv.bluetooth.coordinator.UnknownDeviceCoordinator

enum class DeviceType(val coordinatorClass: Class<out DeviceCoordinator>) {
    UNKNOWN(UnknownDeviceCoordinator::class.java);

    private var coordinator: DeviceCoordinator = coordinatorClass.declaredConstructors.first().newInstance() as DeviceCoordinator

    fun getDeviceCoordinator(): DeviceCoordinator {
        return coordinator
    }

    val isSupported: Boolean
        get() = this != UNKNOWN
}