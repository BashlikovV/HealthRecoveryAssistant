package by.bashlikovvv.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@[Parcelize Serializable]
data class VibrationAction(
    val duration: Long,
    val amplitude: UByte,
) : Parcelable
