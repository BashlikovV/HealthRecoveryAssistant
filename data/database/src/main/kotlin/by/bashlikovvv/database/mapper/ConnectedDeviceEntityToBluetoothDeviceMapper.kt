package by.bashlikovvv.database.mapper

import by.bashlikovvv.database.model.ConnectedDeviceEntity
import by.bashlikovvv.domain.base.Mapper
import by.bashlikovvv.domain.model.BluetoothDevice
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class ConnectedDeviceEntityToBluetoothDeviceMapper : Mapper<ConnectedDeviceEntity, BluetoothDevice> {
    override fun mapFromEntity(entity: ConnectedDeviceEntity): BluetoothDevice {
        return BluetoothDevice(
            id = entity.id,
            name = entity.name,
            address = entity.address,
            type = Json.decodeFromString(entity.type),
        )
    }

    override fun mapToEntity(domain: BluetoothDevice): ConnectedDeviceEntity {
        return ConnectedDeviceEntity(
            id = domain.id,
            name = domain.name,
            address = domain.address,
            type = Json.encodeToString(domain.type),
        )
    }
}