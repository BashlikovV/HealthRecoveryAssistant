package by.bashlikovvv.common.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WearableEventsDbo(
    @SerialName("events") val events: List<WearableEventDbo>,
)

@Serializable
data class WearableEventDbo(
    @SerialName("scheduledTime") val scheduledTime: Long,
    @SerialName("taskDescription") val taskDescriptionDbo: TaskDescriptionDbo,
)

@Serializable
data class TaskDescriptionDbo(
    @SerialName("vibrationDescription") val vibrationDescriptionDbo: VibrationDescriptionDbo,
    @SerialName("notificationText") val notificationText: String?,
)

@Serializable
data class VibrationDescriptionDbo(
    @SerialName("events") val events: List<VibrationEventDbo>,
)

@Serializable
data class VibrationEventDbo(
    @SerialName("duration") val duration: Long,
    @SerialName("amplitude") val amplitude: UByte,
)