package by.bashlikovvv.domain.model

data class WearableEvent(
    val vibrationDescriptor: VibrationDescriptor,
    val notificationText: String? = null,
)