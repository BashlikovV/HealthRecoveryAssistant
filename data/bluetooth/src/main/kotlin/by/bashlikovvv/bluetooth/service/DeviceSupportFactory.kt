package by.bashlikovvv.bluetooth.service

import by.bashlikovvv.bluetooth.model.DeviceSupport
import by.bashlikovvv.bluetooth.model.GBDevice
import by.bashlikovvv.bluetooth.model.QueueEntitiesProvider
import kotlin.reflect.full.primaryConstructor

object DeviceSupportFactory {
    fun createDeviceSupport(
        device: GBDevice,
        queueEntitiesProvider: QueueEntitiesProvider,
    ): DeviceSupport? = createServiceDeviceSupport(device, queueEntitiesProvider)

    private fun  createServiceDeviceSupport(
        device: GBDevice,
        queueEntitiesProvider: QueueEntitiesProvider,
    ): DeviceSupport? {
        val coordinator = device.coordinator
        val supportClass = coordinator.getDeviceSupportClass()

        try {
            return supportClass.primaryConstructor?.call(device, queueEntitiesProvider)
        } catch (_: NoSuchMethodException) {
        } catch (_: ReflectiveOperationException) {
        }

        return null
    }
}