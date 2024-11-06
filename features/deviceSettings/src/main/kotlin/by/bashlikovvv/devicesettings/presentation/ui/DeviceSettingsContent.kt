package by.bashlikovvv.devicesettings.presentation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import by.bashlikovvv.devicesettings.domain.model.NotificationType
import by.bashlikovvv.devicesettings.domain.model.SettingsListItems
import by.bashlikovvv.devicesettings.domain.model.VibrationProfile
import by.bashlikovvv.devicesettings.presentation.ui.component.DeviceSettingsComponent
import by.bashlikovvv.devicesettings.presentation.ui.store.DeviceSettingsStore.Intent
import by.bashlikovvv.devicesettings.presentation.ui.store.DeviceSettingsStore.State
import by.bashlikovvv.ui.composable.ScreenContent
import by.bashlikovvv.ui.dialog.CommonAlertDialog
import by.bashlikovvv.ui.dialog.component.AlertDialogComponent
import kotlinx.collections.immutable.toPersistentList

@Composable
fun DeviceSettingsContent(
    component: DeviceSettingsComponent,
    modifier: Modifier = Modifier,
) {
    ScreenContent(
        contractProvider = component.store,
        initialState = State()
    ) { state, _ ->
        DeviceSettingsScreenContent(
            state = state,
            notificationTypeDialogComponent = component.notificationTypeDialogComponent,
            vibrationProfileDialogComponent = component.vibrationProfileDialogComponent,
            modifier = modifier,
            onSetKey = { dispatchIntent(Intent.SetAuthKey(it)) },
            onVibrationProfile = { test -> dispatchIntent(Intent.SetVibrationCharacteristics(test)) },
            onChooseNotificationType = { component.notificationTypeDialogComponent.showDialog() },
            onChooseVibrationProfile = { component.vibrationProfileDialogComponent.showDialog() },
            setNotificationType = { dispatchIntent(Intent.SetNotificationType(it)) },
            setVibrationProfile = { dispatchIntent(Intent.SetVibrationProfile(it)) }
        )
    }
}

@Composable
private fun DeviceSettingsScreenContent(
    state: State,
    notificationTypeDialogComponent: AlertDialogComponent,
    vibrationProfileDialogComponent: AlertDialogComponent,
    modifier: Modifier = Modifier,
    onSetKey: (String) -> Unit,
    onVibrationProfile: (test: Boolean) -> Unit,
    onChooseNotificationType: () -> Unit,
    onChooseVibrationProfile: () -> Unit,
    setNotificationType: (NotificationType) -> Unit,
    setVibrationProfile: (VibrationProfile) -> Unit,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        val items = mutableListOf<SettingsListItems>().apply {
            if (state.keySettings != null) add(state.keySettings)
            if (state.vibrationSettings != null) add(state.vibrationSettings)
        }.toPersistentList()
        if (items.isNotEmpty()) {
            SettingsList(
                list = items,
                onVibrationProfile = onVibrationProfile,
                onSetKey = onSetKey,
                onChooseNotificationType = onChooseNotificationType,
                onChooseVibrationProfile = onChooseVibrationProfile
            )
        }
        CommonAlertDialog(component = notificationTypeDialogComponent) {
            NotificationTypeDialogContent(
                notificationType = state.notificationType,
                onConfirm = setNotificationType
            )
        }
        CommonAlertDialog(component = vibrationProfileDialogComponent) {
            VibrationProfileDialogContent(
                vibrationProfile = state.vibrationProfile,
                onConfirm = setVibrationProfile
            )
        }
    }
}