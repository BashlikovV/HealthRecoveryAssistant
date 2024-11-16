package by.bashlikovvv.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@[Parcelize Serializable SerialName("VibrationDescriptor")]
data class VibrationDescriptor(
    @SerialName("onOffSequence") val onOffSequence: IntArray,
    @SerialName("repeat") val repeat: Short,
    @SerialName("alertLevel") val alertLevel: Int,
) : Parcelable
