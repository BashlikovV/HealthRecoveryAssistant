package by.bashlikovvv.devicesettings.presentation.ui.store

import by.bashlikovvv.devicesettings.domain.model.NotificationType
import by.bashlikovvv.devicesettings.domain.model.VibrationProfile
import by.bashlikovvv.devicesettings.presentation.ui.store.DeviceSettingsStore.Intent
import by.bashlikovvv.devicesettings.presentation.ui.store.DeviceSettingsStore.State
import by.bashlikovvv.ui.base.BaseStoreFactory
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory

class DeviceSettingsStoreFactory(
    storeFactory: StoreFactory,
) : BaseStoreFactory<DeviceSettingsStore>(storeFactory) {
    override fun create(): DeviceSettingsStore = DeviceSettingsStoreImpl()

    private inner class DeviceSettingsStoreImpl :
        DeviceSettingsStore, Store<Intent, State, Nothing> by storeFactory.create(
            name = STORE_NAME,
            initialState = State(),
            executorFactory = ::DeviceSettingsStoreExecutor,
            reducer = reducerImpl,
        )

    private val reducerImpl = Reducer<State, Msg> { msg ->
        when(msg) {
            is Msg.SetNotificationType -> reduce(msg)
            is Msg.SetVibrationProfile -> reduce(msg)
        }
    }

    private fun State.reduce(msg: Msg.SetNotificationType): State {
        return this.copy(
            notificationType = msg.notificationType,
            vibrationSettings = vibrationSettings?.copy(
                notificationType = msg.notificationType
            )
        )
    }

    private fun State.reduce(msg: Msg.SetVibrationProfile): State {
        return this.copy(
            vibrationProfile = msg.vibrationProfile,
            vibrationSettings = vibrationSettings?.copy(
                vibrationProfile = msg.vibrationProfile,
            )
        )
    }

    internal sealed class Msg {
        data class SetNotificationType(val notificationType: NotificationType) : Msg()

        data class SetVibrationProfile(val vibrationProfile: VibrationProfile) : Msg()
    }

    companion object {
        const val STORE_NAME = "DeviceSettingsStore"
    }
}