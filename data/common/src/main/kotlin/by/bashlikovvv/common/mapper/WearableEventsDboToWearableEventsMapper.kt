package by.bashlikovvv.common.mapper

import by.bashlikovvv.common.model.TaskDescriptionDbo
import by.bashlikovvv.common.model.VibrationProfileDbo
import by.bashlikovvv.common.model.WearableEventDbo
import by.bashlikovvv.common.model.WearableEventsDbo
import by.bashlikovvv.domain.base.Mapper
import by.bashlikovvv.domain.model.VibrationDescriptor
import by.bashlikovvv.domain.model.WearableEvent
import by.bashlikovvv.domain.model.WearableEvents

class WearableEventsDboToWearableEventsMapper : Mapper<WearableEventsDbo, WearableEvents> {
    override fun mapFromEntity(entity: WearableEventsDbo): WearableEvents {
        return WearableEvents(
            events = entity.events.map { event ->
                WearableEvent(
                    scheduledTime = event.scheduledTime,
                    notificationText = event.taskDescription.text,
                    vibrationDescriptor = event.taskDescription.vibrationProfile.let { vibrationProfile ->
                        VibrationDescriptor(
                            onOffSequence = vibrationProfile.onOffSequence,
                            repeat = vibrationProfile.repeat,
                            alertLevel = vibrationProfile.alertLevel,
                        )
                    }
                )
            }
        )
    }

    override fun mapToEntity(domain: WearableEvents): WearableEventsDbo {
        return WearableEventsDbo(
            events = domain.events.map { event ->
                WearableEventDbo(
                    scheduledTime = event.scheduledTime,
                    taskDescription = TaskDescriptionDbo(
                        vibrationProfile = event.vibrationDescriptor.let { vibrationDescriptor ->
                            VibrationProfileDbo(
                                onOffSequence = vibrationDescriptor.onOffSequence,
                                repeat = vibrationDescriptor.repeat,
                                alertLevel = vibrationDescriptor.alertLevel,
                            )
                        },
                        text = event.notificationText,
                    )
                )
            }
        )
    }
}