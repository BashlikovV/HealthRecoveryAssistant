package by.bashlikovvv.discovery.presentation.ui

import android.app.Activity.RESULT_OK
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import by.bashlikovvv.discovery.domain.contract.BluetoothReceiver
import by.bashlikovvv.discovery.domain.contract.BondingReceiver
import by.bashlikovvv.discovery.domain.model.DiscoveryListItems
import by.bashlikovvv.discovery.presentation.ui.component.DiscoveryComponent
import by.bashlikovvv.discovery.presentation.ui.store.DiscoveryStore
import by.bashlikovvv.discovery.presentation.ui.store.DiscoveryStore.*
import by.bashlikovvv.ui.composable.ScreenContent
import by.bashlikovvv.ui.dialog.CommonAlertDialog
import by.bashlikovvv.ui.res.AppRes
import by.bashlikovvv.ui.theme.HealthRecoveryAssistantTheme
import kotlinx.collections.immutable.persistentListOf
import java.lang.IllegalArgumentException

@Composable
fun DiscoveryContent(
    component: DiscoveryComponent,
    modifier: Modifier = Modifier,
) {
    ScreenContent(
        contractProvider = component.store,
        initialState = State(),
    ) { state, label ->
        var dialogTitle: String by remember { mutableStateOf("") }
        val context = LocalContext.current
        val requestMultiplePermissionsLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions()
        ) { map ->
            map.forEach { (permission, granted) ->
                dispatchIntent(DiscoveryStore.Intent.OnPermissionResult(permission, granted))
            }
            if (map.containsValue(false)) {
                dialogTitle = "Permissions not granted"
                component.alertDialogComponent.showDialog()
            }
        }
        val startActivityResultLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                dispatchIntent(DiscoveryStore.Intent.StartDiscovery)
            }
        }
        DisposableEffect(Unit) {
            registerBondReceiver(context, component.bondingReceiver)
            onDispose {
                context.unregisterReceiverWithCheck(component.bluetoothReceiver)
                context.unregisterReceiverWithCheck(component.bondingReceiver)
            }
        }
        LabelProcessionBlock(
            label = label,
            startActivityResultLauncher = startActivityResultLauncher,
            requestPermission = { requestMultiplePermissionsLauncher.launch(arrayOf(it)) },
            cancelDiscovery = {
                context.unregisterReceiverWithCheck(component.bluetoothReceiver)
            },
            startDiscovery = {
                registerBluetoothReceiver(context, component.bluetoothReceiver)
            }
        )
        DiscoveryScreenContent(
            state = state,
            modifier = modifier,
            onDiscoveryClicked = { dispatchIntent(DiscoveryStore.Intent.DiscoveryButtonClicked) },
            onDeviceClicked = { address -> dispatchIntent(DiscoveryStore.Intent.BondDevice(address)) },
        )
        CommonAlertDialog(
            component = component.alertDialogComponent,
            confirmButton = {
                Button(onClick = { dismiss() }) {
                    Text(AppRes.strings.ok)
                }
            },
            title = { Text(dialogTitle) },
        )
    }
}

@Composable
private fun LabelProcessionBlock(
    label: Label?,
    startActivityResultLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>,
    requestPermission: (String) -> Unit,
    startDiscovery: () -> Unit,
    cancelDiscovery: () -> Unit,
) {
    LaunchedEffect(label) {
        when (label) {
            Label.TurnOnBluetooth -> startActivityResultLauncher.launch(
                Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            )
            is Label.RequestPermission -> requestPermission(label.permission)
            is Label.CancelDiscovery -> cancelDiscovery()
            is Label.StartDiscovery -> startDiscovery()
            else -> Unit
        }
    }
}

private fun registerBondReceiver(
    context: Context,
    receiver: BondingReceiver
) {
    val bondingIntents = IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
    ContextCompat.registerReceiver(
        context,
        receiver,
        bondingIntents,
        ContextCompat.RECEIVER_EXPORTED
    )
}

private fun Context.unregisterReceiverWithCheck(receiver: BroadcastReceiver) {
    try {
        unregisterReceiver(receiver)
    } catch (_: IllegalArgumentException) {
    }
}

private fun registerBluetoothReceiver(
    context: Context,
    receiver: BluetoothReceiver,
) {
    val bluetoothIntents = IntentFilter().apply {
        addAction(BluetoothDevice.ACTION_FOUND)
        addAction(BluetoothDevice.ACTION_UUID)
        addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
        addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
        addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
    }
    ContextCompat.registerReceiver(
        context,
        receiver,
        bluetoothIntents,
        ContextCompat.RECEIVER_EXPORTED
    )
}

@[Composable Preview]
private fun DiscoveryScreenContentPreview() {
    HealthRecoveryAssistantTheme {
        DiscoveryScreenContent(
            state = State(
                isScanning = true,
                devices = persistentListOf(
                    DiscoveryListItems.Device(
                        id = 1,
                        name = "Name",
                        address = "Address",
                    ),
                )
            ),
            onDiscoveryClicked = {},
            onDeviceClicked = {},
        )
    }
}