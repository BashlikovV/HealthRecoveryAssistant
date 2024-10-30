package by.bashlikovvv.bluetooth.model

import by.bashlikovvv.bluetooth.devices.unknown.UnknownDeviceCoordinator
import kotlin.reflect.KClass

enum class DeviceType(val coordinatorClass: KClass<out DeviceCoordinator>) {
    UNKNOWN(UnknownDeviceCoordinator::class),
    MI_BAND_5(MiBand5Coordinator::class),
}