package by.bashlikovvv.database.mapper

import by.bashlikovvv.database.model.WearableEventEntity
import by.bashlikovvv.domain.base.Mapper
import by.bashlikovvv.domain.model.WearableEvent

class WearableEventToWearableEventEntityMapper(
    private val taskDescription: Long
) : Mapper<WearableEvent, WearableEventEntity> {
    override fun mapFromEntity(entity: WearableEvent): WearableEventEntity {
        return WearableEventEntity(
            id = 0,
            scheduledTime = entity.scheduledTime,
            taskDescription = taskDescription
        )
    }

    override fun mapToEntity(domain: WearableEventEntity): WearableEvent {
        throw NotImplementedError()
    }
}