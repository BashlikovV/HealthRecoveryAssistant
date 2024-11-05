package by.bashlikovvv.home.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class DevicesListItems(open val id: Int) : Parcelable {
    @Parcelize
    data class Device(
        override val id: Int,
        val name: String,
        val address: String,
        val connected: Boolean,
    ) : DevicesListItems(id)
}