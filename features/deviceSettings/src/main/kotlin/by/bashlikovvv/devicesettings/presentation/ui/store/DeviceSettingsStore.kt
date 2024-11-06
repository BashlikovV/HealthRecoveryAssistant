package by.bashlikovvv.devicesettings.presentation.ui.store

import android.os.Parcelable
import by.bashlikovvv.devicesettings.domain.model.NotificationType
import by.bashlikovvv.devicesettings.domain.model.SettingsListItems
import by.bashlikovvv.devicesettings.domain.model.VibrationProfile
import by.bashlikovvv.devicesettings.presentation.ui.store.DeviceSettingsStore.Intent
import by.bashlikovvv.devicesettings.presentation.ui.store.DeviceSettingsStore.State
import by.bashlikovvv.domain.model.BluetoothDevice
import com.arkivanov.mvikotlin.core.store.Store
import kotlinx.parcelize.Parcelize

interface DeviceSettingsStore : Store<Intent, State, Nothing> {
    sealed class Intent {
        data class SetAuthKey(val authKey: String) : Intent()

        data class SetVibrationCharacteristics(
            val test: Boolean,
        ) : Intent()

        data class SetNotificationType(
            val notificationType: NotificationType,
        ) : Intent()

        data class SetVibrationProfile(
            val vibrationProfile: VibrationProfile,
        ) : Intent()
    }

    @Parcelize
    data class State(
        val device: BluetoothDevice? = null,
        val notificationType: NotificationType = NotificationType.GoalNotification(),
        val vibrationProfile: VibrationProfile = VibrationProfile.staccato(1),
        val keySettings: SettingsListItems.ItemKeySettings? = SettingsListItems.ItemKeySettings(
            0,
            "0x2752cc9ca28106d7d9b128119c50c9f9"
        ),
        val vibrationSettings: SettingsListItems.ItemVibrationProfileSettings? = SettingsListItems.ItemVibrationProfileSettings(
            1,
            notificationType,
            vibrationProfile
        ),
    ) : Parcelable
}