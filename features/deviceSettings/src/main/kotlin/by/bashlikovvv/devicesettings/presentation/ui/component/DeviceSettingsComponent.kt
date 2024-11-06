package by.bashlikovvv.devicesettings.presentation.ui.component

import by.bashlikovvv.devicesettings.presentation.ui.store.DeviceSettingsStore
import by.bashlikovvv.ui.dialog.component.AlertDialogComponent

interface DeviceSettingsComponent {
    val store: DeviceSettingsStore

    val notificationTypeDialogComponent: AlertDialogComponent

    val vibrationProfileDialogComponent: AlertDialogComponent
}