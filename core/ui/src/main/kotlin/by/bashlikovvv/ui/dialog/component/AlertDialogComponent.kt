package by.bashlikovvv.ui.dialog.component

import by.bashlikovvv.ui.dialog.store.AlertDialogStore

interface AlertDialogComponent {
    val store: AlertDialogStore

    fun showDialog(
        title: String = "",
        text: String = "",
        confirmButton: String? = null,
        dismissButton: String? = null,
    )
}