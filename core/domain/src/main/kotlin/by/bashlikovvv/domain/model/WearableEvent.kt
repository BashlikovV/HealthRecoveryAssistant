package by.bashlikovvv.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@[Parcelize Serializable]
data class WearableEvent(
    val vibrationDescriptor: VibrationDescriptor,
    val notificationText: String? = null,
    val scheduledTime: Long,
) : Parcelable