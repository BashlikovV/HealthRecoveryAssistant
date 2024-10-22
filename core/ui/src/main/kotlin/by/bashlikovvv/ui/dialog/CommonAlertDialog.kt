package by.bashlikovvv.ui.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import by.bashlikovvv.ui.composable.ScreenContent
import by.bashlikovvv.ui.dialog.component.AlertDialogComponent
import by.bashlikovvv.ui.dialog.store.AlertDialogStore

@Composable
fun CommonAlertDialog(
    component: AlertDialogComponent,
    modifier: Modifier = Modifier,
    onConfirm: () -> Unit = {},
    onDismiss: () -> Unit = {},
) {
    ScreenContent(
        contractProvider = component.store,
        initialState = AlertDialogStore.State(),
    ) { state, label ->
        if (state.isVisible) {
            AlertDialog(
                onDismissRequest = { dispatchIntent(AlertDialogStore.Intent.HideDialog) },
                title = { Text(state.title) },
                text = { Text(state.text) },
                confirmButton = {
                    state.confirmButton?.let {
                        Button(
                            onClick = {
                                dispatchIntent(AlertDialogStore.Intent.HideDialog)
                                onConfirm()
                            }
                        ) {
                            Text(it)
                        }
                    }
                },
                dismissButton = state.dismissButton?.let {
                    {
                        Button(
                            onClick = {
                                dispatchIntent(AlertDialogStore.Intent.HideDialog)
                                onDismiss()
                            }
                        ) {
                            Text(it)
                        }
                    }
                },
                modifier = modifier,
            )
        }
    }
}