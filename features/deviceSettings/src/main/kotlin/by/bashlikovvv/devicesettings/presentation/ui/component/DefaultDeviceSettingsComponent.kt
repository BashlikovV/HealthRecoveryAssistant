package by.bashlikovvv.devicesettings.presentation.ui.component

import by.bashlikovvv.devicesettings.presentation.ui.store.DeviceSettingsStore
import by.bashlikovvv.devicesettings.presentation.ui.store.DeviceSettingsStoreFactory
import by.bashlikovvv.ui.base.BaseComponent
import by.bashlikovvv.ui.dialog.component.AlertDialogComponent
import by.bashlikovvv.ui.dialog.component.DefaultAlertDialogComponent
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory

class DefaultDeviceSettingsComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
) : DeviceSettingsComponent, BaseComponent(componentContext) {
    override val store: DeviceSettingsStore = instanceKeeper.getStore {
        DeviceSettingsStoreFactory(storeFactory).create()
    }
    override val notificationTypeDialogComponent: AlertDialogComponent =
        DefaultAlertDialogComponent(
            componentContext = childContext(NOTIFICATION_TYPE_DIALOG),
            storeFactory = storeFactory
        )

    override val vibrationProfileDialogComponent: AlertDialogComponent =
        DefaultAlertDialogComponent(
            componentContext = childContext(VIBRATION_PROFILE_DIALOG),
            storeFactory = storeFactory
        )

    companion object {
        const val NOTIFICATION_TYPE_DIALOG = "NotificationTypeDialog"

        const val VIBRATION_PROFILE_DIALOG = "VibrationProfileDialog"
    }
}