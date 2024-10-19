package by.bashlikovvv.common.mapper

import by.bashlikovvv.common.model.TaskDescriptionDbo
import by.bashlikovvv.common.model.VibrationDescriptionDbo
import by.bashlikovvv.common.model.VibrationEventDbo
import by.bashlikovvv.common.model.WearableEventDbo
import by.bashlikovvv.common.model.WearableEventsDbo
import by.bashlikovvv.domain.base.Mapper
import by.bashlikovvv.domain.model.VibrationAction
import by.bashlikovvv.domain.model.VibrationDescriptor
import by.bashlikovvv.domain.model.WearableEvent
import by.bashlikovvv.domain.model.WearableEvents

class WearableEventsDboToWearableEventsMapper : Mapper<WearableEventsDbo, WearableEvents> {
    override fun mapFromEntity(entity: WearableEventsDbo): WearableEvents {
        return WearableEvents(
            events = entity.events.map { event ->
                WearableEvent(
                    scheduledTime = event.scheduledTime,
                    notificationText = event.taskDescriptionDbo.notificationText,
                    vibrationDescriptor = VibrationDescriptor(
                        actions = event.taskDescriptionDbo.vibrationDescriptionDbo.events.map {
                            VibrationAction(
                                duration = it.duration,
                                amplitude = it.amplitude,
                            )
                        }
                    )
                )
            }
        )
    }

    override fun mapToEntity(domain: WearableEvents): WearableEventsDbo {
        return WearableEventsDbo(
            events = domain.events.map { event ->
                WearableEventDbo(
                    scheduledTime = event.scheduledTime,
                    taskDescriptionDbo = TaskDescriptionDbo(
                        vibrationDescriptionDbo = VibrationDescriptionDbo(
                            events = event.vibrationDescriptor.actions.map {
                                VibrationEventDbo(
                                    duration = it.duration,
                                    amplitude = it.amplitude
                                )
                            },
                        ),
                        notificationText = event.notificationText,
                    ),
                )
            }
        )
    }
}