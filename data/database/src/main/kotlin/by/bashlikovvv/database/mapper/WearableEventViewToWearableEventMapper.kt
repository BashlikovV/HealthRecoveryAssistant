package by.bashlikovvv.database.mapper

import by.bashlikovvv.database.views.WearableEventView
import by.bashlikovvv.domain.model.WearableEvent
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class WearableEventViewToWearableEventMapper :
    by.bashlikovvv.domain.base.Mapper<WearableEventView, WearableEvent> {
    override fun mapFromEntity(entity: WearableEventView): WearableEvent {
        return WearableEvent(
            notificationText = entity.notificationText,
            vibrationDescriptor = Json.Default.decodeFromString(entity.vibrationEventsString),
            scheduledTime = entity.scheduledTime,
        )
    }

    override fun mapToEntity(domain: WearableEvent): WearableEventView {
        return WearableEventView(
            id = 0,
            scheduledTime = domain.scheduledTime,
            vibrationEventsString = Json.Default.encodeToString(domain.vibrationDescriptor),
            notificationText = domain.notificationText
        )
    }
}