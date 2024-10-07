package by.bashlikovvv.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@[Parcelize Serializable]
data class VibrationDescriptor(
    val actions: List<VibrationAction>
) : Parcelable
