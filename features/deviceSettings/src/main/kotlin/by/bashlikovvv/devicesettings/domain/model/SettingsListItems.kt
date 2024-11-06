package by.bashlikovvv.devicesettings.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class SettingsListItems(open val id: Int) : Parcelable {
    @Parcelize
    data class ItemKeySettings(
        override val id: Int,
        val key: String,
    ) : SettingsListItems(id)

    @Parcelize
    data class ItemVibrationProfileSettings(
        override val id: Int,
        val notificationType: NotificationType,
        val vibrationProfile: VibrationProfile,
    ) : SettingsListItems(id)
}