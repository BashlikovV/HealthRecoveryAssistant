package by.bashlikovvv.ui.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.window.DialogProperties
import by.bashlikovvv.ui.composable.ScreenContent
import by.bashlikovvv.ui.dialog.component.AlertDialogComponent
import by.bashlikovvv.ui.dialog.store.AlertDialogStore

@Composable
fun CommonAlertDialog(
    component: AlertDialogComponent,
    confirmButton: @Composable AlertDialogScope.() -> Unit,
    modifier: Modifier = Modifier,
    dismissButton: @Composable (AlertDialogScope.() -> Unit)? = null,
    icon: @Composable (() -> Unit)? = null,
    title: @Composable (() -> Unit)? = null,
    text: @Composable (() -> Unit)? = null,
    shape: Shape = AlertDialogDefaults.shape,
    containerColor: Color = AlertDialogDefaults.containerColor,
    iconContentColor: Color = AlertDialogDefaults.iconContentColor,
    titleContentColor: Color = AlertDialogDefaults.titleContentColor,
    textContentColor: Color = AlertDialogDefaults.textContentColor,
    tonalElevation: Dp = AlertDialogDefaults.TonalElevation,
    properties: DialogProperties = DialogProperties()
) {
    ScreenContent(
        contractProvider = component.store,
        initialState = AlertDialogStore.State(),
    ) { state, label ->
        if (state.isVisible) {
            val scope = object : AlertDialogScope {
                override fun dismiss() {
                    dispatchIntent(AlertDialogStore.Intent.HideDialog)
                }
            }
            AlertDialog(
                onDismissRequest = { dispatchIntent(AlertDialogStore.Intent.HideDialog) },
                title = title,
                text = text,
                icon = icon,
                confirmButton = @Composable { scope.confirmButton() },
                dismissButton = dismissButton?.let { @Composable { scope.it() } },
                shape = shape,
                containerColor = containerColor,
                iconContentColor = iconContentColor,
                titleContentColor = titleContentColor,
                textContentColor = textContentColor,
                tonalElevation = tonalElevation,
                properties = properties,
                modifier = modifier,
            )
        }
    }
}