package by.bashlikovvv.discovery.presentation.ui

import android.Manifest
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
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
import by.bashlikovvv.util.isPermissionGranted
import by.bashlikovvv.util.requestPermissionsCompat

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
            registerBluetoothReceiver(context, component.bluetoothReceiver)
            registerBondReceiver(context, component.bondingReceiver)
            requestPermissions(context as Activity, requestMultiplePermissionsLauncher)
            onDispose {
                context.unregisterReceiverWithCheck(component.bluetoothReceiver)
                context.unregisterReceiverWithCheck(component.bondingReceiver)
            }
        }
        LabelProcessionBlock(label, startActivityResultLauncher)
        DiscoveryScreenContent(
            state = state,
            modifier = modifier,
            onDiscoveryClicked = { dispatchIntent(DiscoveryStore.Intent.DiscoveryButtonClicked) },
            onDeviceClicked = { address -> dispatchIntent(DiscoveryStore.Intent.BondDevice(address)) },
            onVibrate = { dispatchIntent(DiscoveryStore.Intent.Vibrate) }
        )
        CommonAlertDialog(
            component = component.alertDialogComponent,
            confirmButton = {
                Button(onClick = { dismiss() }) {
                    Text(AppRes.strings.ok)
                }
            },
            title = { Text(dialogTitle) }
        )
    }
}

@Composable
private fun LabelProcessionBlock(
    label: Label?,
    startActivityResultLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>
) {
    LaunchedEffect(label) {
        when (label) {
            Label.TurnOnBluetooth -> startActivityResultLauncher.launch(
                Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            )

            null -> Unit
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
    } catch (_: IllegalStateException) {
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

private fun requestPermissions(
    activity: Activity,
    requestMultiplePermissionsLauncher: ActivityResultLauncher<Array<String>>,
) {
    val wantedPermissions = mutableListOf<String>()
    if (!activity.isPermissionGranted(Manifest.permission.ACCESS_COARSE_LOCATION)) {
        wantedPermissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)
    }
    if (!activity.isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
        wantedPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        if (!activity.isPermissionGranted(Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
            wantedPermissions.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }
    }
    if (wantedPermissions.isNotEmpty()) {
        wantedPermissions.forEach {
            activity.requestPermissionsCompat(arrayOf(it))
        }
        wantedPermissions.clear()
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (!activity.isPermissionGranted(Manifest.permission.BLUETOOTH)) {
            wantedPermissions.add(Manifest.permission.BLUETOOTH)
        }
        if (!activity.isPermissionGranted(Manifest.permission.BLUETOOTH_ADMIN)) {
            wantedPermissions.add(Manifest.permission.BLUETOOTH_ADMIN)
        }
        if (!activity.isPermissionGranted(Manifest.permission.BLUETOOTH_SCAN)) {
            wantedPermissions.add(Manifest.permission.BLUETOOTH_SCAN)
        }
        if (!activity.isPermissionGranted(Manifest.permission.BLUETOOTH_CONNECT)) {
            wantedPermissions.add(Manifest.permission.BLUETOOTH_CONNECT)
        }
    }
    if (wantedPermissions.isNotEmpty()) {
        requestMultiplePermissionsLauncher.launch(wantedPermissions.toTypedArray())
    }
}

@[Composable Preview]
private fun DiscoveryScreenContentPreview() {
    HealthRecoveryAssistantTheme {
        DiscoveryScreenContent(
            state = State(
                isScanning = true,
                devices = listOf(
                    DiscoveryListItems.Device(
                        id = 1,
                        name = "Name",
                        address = "Address",
                    ),
                )
            ),
            onDiscoveryClicked = {},
            onDeviceClicked = {},
            onVibrate = {}
        )
    }
}