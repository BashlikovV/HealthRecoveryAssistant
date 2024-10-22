package by.bashlikovvv.discovery.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class DiscoveryListItems(open val id: Int) : Parcelable {
    @Parcelize
    data class Device(
        override val id: Int,
        val name: String,
        val address: String,
    ) : DiscoveryListItems(id)

    @Parcelize
    data class Progress(
        override val id: Int,
    ) : DiscoveryListItems(id)
}