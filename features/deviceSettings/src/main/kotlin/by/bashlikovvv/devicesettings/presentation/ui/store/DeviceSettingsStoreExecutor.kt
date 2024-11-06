package by.bashlikovvv.devicesettings.presentation.ui.store

import by.bashlikovvv.common.repository.BluetoothRepository
import by.bashlikovvv.devicesettings.domain.model.NotificationType
import by.bashlikovvv.devicesettings.domain.model.VibrationProfile
import by.bashlikovvv.devicesettings.presentation.ui.store.DeviceSettingsStore.Intent
import by.bashlikovvv.devicesettings.presentation.ui.store.DeviceSettingsStore.State
import by.bashlikovvv.devicesettings.presentation.ui.store.DeviceSettingsStoreFactory.Msg
import by.bashlikovvv.ui.base.BaseCoroutineExecutor
import org.koin.core.component.inject

internal class DeviceSettingsStoreExecutor : BaseCoroutineExecutor<Intent, Nothing, State, Msg, Nothing>() {
    private val bluetoothRepository: BluetoothRepository by inject()

    override fun executeIntent(intent: Intent, getState: () -> State) {
        when(intent) {
            is Intent.SetAuthKey -> setAuthKey(intent.authKey)
            is Intent.SetVibrationCharacteristics -> setVibrationCharacteristics(getState(), intent)
            is Intent.SetNotificationType -> setNotificationType(intent.notificationType)
            is Intent.SetVibrationProfile -> setVibrationProfile(intent.vibrationProfile)
        }
    }

    private fun setAuthKey(authKey: String) {}

    private fun setVibrationCharacteristics(
        state: State,
        intent: Intent.SetVibrationCharacteristics,
    ) {
        setVibrationProfile(
            notificationType = state.notificationType,
            vibrationProfile = state.vibrationProfile,
            test = intent.test,
        )
    }

    private fun setVibrationProfile(
        notificationType: NotificationType,
        vibrationProfile: VibrationProfile,
        test: Boolean
    ) = launchIO {
        bluetoothRepository.setVibrationProfile(
            notificationType = notificationType.toDomainNotificationType(),
            test = test,
            repeat = vibrationProfile.repeat,
            onOffSequence = vibrationProfile.onOffSequence
        )
    }

    private fun setNotificationType(notificationType: NotificationType) {
        dispatch(Msg.SetNotificationType(notificationType))
    }

    private fun setVibrationProfile(vibrationProfile: VibrationProfile) {
        dispatch(Msg.SetVibrationProfile(vibrationProfile))
    }
}