package by.bashlikovvv.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class WearableEvents(
    val events: List<WearableEvent>,
) : Parcelable