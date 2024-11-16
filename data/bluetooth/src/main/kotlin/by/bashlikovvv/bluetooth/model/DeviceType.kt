package by.bashlikovvv.bluetooth.model

import by.bashlikovvv.bluetooth.devices.miband.MiBand5Coordinator
import by.bashlikovvv.bluetooth.devices.unknown.UnknownDeviceCoordinator
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

enum class DeviceType(val coordinatorClass: KClass<out DeviceCoordinator>) {
    UNKNOWN(UnknownDeviceCoordinator::class),
    MI_BAND_5(MiBand5Coordinator::class);

    fun getDeviceCoordinator(): DeviceCoordinator {
        return try {
            coordinatorClass.createInstance()
        } catch (e: ReflectiveOperationException) {
            throw RuntimeException(e)
        }
    }
}