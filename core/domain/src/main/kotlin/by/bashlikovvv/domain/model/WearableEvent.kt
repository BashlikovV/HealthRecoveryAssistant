package by.bashlikovvv.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@[Parcelize Serializable SerialName("WearableEvent")]
data class WearableEvent(
    @SerialName("vibrationDescriptor") val vibrationDescriptor: VibrationDescriptor,
    @SerialName("notificationText") val notificationText: String,
    @SerialName("scheduledTime") val scheduledTime: Long,
) : Parcelable