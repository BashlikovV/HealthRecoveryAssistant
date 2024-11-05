package by.bashlikovvv.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@[Parcelize Serializable]
data class VibrationDescriptor(
    val onOffSequence: IntArray,
    val repeat: Short,
    val alertLevel: Int,
) : Parcelable
