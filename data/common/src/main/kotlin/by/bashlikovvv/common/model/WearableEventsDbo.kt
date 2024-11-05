package by.bashlikovvv.common.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@[Serializable SerialName("WearableEvents")]
data class WearableEventsDbo(
    @SerialName("events") val events: List<WearableEventDbo>,
)

@[Serializable SerialName("WearableEvent")]
data class WearableEventDbo(
    @SerialName("scheduledTime") val scheduledTime: Long,
    @SerialName("taskDescription") val taskDescription: TaskDescriptionDbo,
)

@[Serializable SerialName("TaskDescription")]
data class TaskDescriptionDbo(
    @SerialName("vibrationProfile") val vibrationProfile: VibrationProfileDbo,
    @SerialName("text") val text: String
)

@[Serializable SerialName("AlertLevel")]
enum class AlertLevel(@SerialName("id") val id: Int) {
    NoAlert(0),
    MildAlert(1),
    HighAlert(2);
    // 3-255 reserved
}

@[Serializable SerialName("VibrationProfile")]
data class VibrationProfileDbo(
    @SerialName("onOffSequence") val onOffSequence: IntArray,
    @SerialName("repeat") val repeat: Short,
    @SerialName("alertLevel") val alertLevel: Int = AlertLevel.MildAlert.id,
) {
    init {
        require(onOffSequence.size % 2 == 0) { "Each on duration must have a subsequent off duration" }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as VibrationProfileDbo

        if (!onOffSequence.contentEquals(other.onOffSequence)) return false
        if (repeat != other.repeat) return false
        if (alertLevel != other.alertLevel) return false

        return true
    }

    override fun hashCode(): Int {
        var result = onOffSequence.contentHashCode()
        result = 31 * result + repeat
        result = 31 * result + alertLevel
        return result
    }
}