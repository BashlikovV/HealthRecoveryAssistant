package by.bashlikovvv.common.mapper

import by.bashlikovvv.common.local.model.WearableTaskEntity
import by.bashlikovvv.domain.base.Mapper
import by.bashlikovvv.domain.model.WearableEvent
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class WearableEventToWearableTaskEntityMapper : Mapper<WearableEvent, WearableTaskEntity> {
    override fun mapFromEntity(entity: WearableEvent): WearableTaskEntity {
        entity.vibrationDescriptor
        return WearableTaskEntity(
            id = 0,
            vibrationEvents = Json.encodeToString(entity.vibrationDescriptor),
            notificationText = entity.notificationText
        )
    }

    override fun mapToEntity(domain: WearableTaskEntity): WearableEvent {
        throw NotImplementedError()
    }
}