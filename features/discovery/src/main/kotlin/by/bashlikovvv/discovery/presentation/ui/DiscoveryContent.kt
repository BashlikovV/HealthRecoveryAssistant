package by.bashlikovvv.discovery.presentation.ui

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import by.bashlikovvv.discovery.domain.contract.BluetoothReceiver
import by.bashlikovvv.discovery.domain.model.DiscoveryListItems
import by.bashlikovvv.discovery.presentation.ui.component.DiscoveryComponent
import by.bashlikovvv.discovery.presentation.ui.store.DiscoveryStore
import by.bashlikovvv.ui.composable.ScreenContent
import by.bashlikovvv.ui.dialog.CommonAlertDialog
import by.bashlikovvv.ui.theme.HealthRecoveryAssistantTheme

@Composable
fun DiscoveryContent(
    component: DiscoveryComponent,
    modifier: Modifier = Modifier,
) {
    ScreenContent(
        contractProvider = component.store,
        initialState = DiscoveryStore.State(),
    ) { state, label ->
        val context = LocalContext.current
        val requestMultiplePermissionsLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions()
        ) { map ->
            if (map.containsValue(false)) {
                dispatchIntent(
                    DiscoveryStore.Intent.ShowDialog(
                        "Not all permissions granted",
                        confirmButton = "Ok"
                    )
                )
            }
        }
        DisposableEffect(Unit) {
            registerBluetoothReceiver(context, component.bluetoothReceiver)
            requestPermissions(context as Activity, requestMultiplePermissionsLauncher)
            onDispose { unregisterBluetoothReceiver(context, component.bluetoothReceiver) }
        }
        DiscoveryScreenContent(
            state = state,
            modifier = modifier,
            onDiscoveryClicked = { dispatchIntent(DiscoveryStore.Intent.DiscoveryButtonClicked) }
        )
        CommonAlertDialog(
            component = component.alertDialogComponent
        )
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

private fun unregisterBluetoothReceiver(
    context: Context,
    receiver: BluetoothReceiver,
) {
    try {
        context.unregisterReceiver(receiver)
    } catch (_: IllegalStateException) {
    }
}

private fun requestPermissions(
    activity: Activity,
    requestMultiplePermissionsLauncher: ActivityResultLauncher<Array<String>>,
) {
    val wantedPermissions = mutableListOf<String>()
    if (ActivityCompat.checkSelfPermission(
            activity,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        wantedPermissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)
    }
    if (ActivityCompat.checkSelfPermission(
            activity,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        wantedPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
    }
    if (wantedPermissions.isNotEmpty()) {
        ActivityCompat.requestPermissions(activity, wantedPermissions.toTypedArray(), 0)
        wantedPermissions.clear()
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                0
            )
        }
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            wantedPermissions.add(Manifest.permission.BLUETOOTH_SCAN)
        }
        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            wantedPermissions.add(Manifest.permission.BLUETOOTH_CONNECT)
        }
    }
    if (wantedPermissions.isNotEmpty()) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            ActivityCompat.requestPermissions(activity, wantedPermissions.toTypedArray(), 0)
        } else {
            requestMultiplePermissionsLauncher.launch(wantedPermissions.toTypedArray())
        }
    }
}

@[Composable Preview]
private fun DiscoveryScreenContentPreview() {
    HealthRecoveryAssistantTheme {
        DiscoveryScreenContent(
            state = DiscoveryStore.State(
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
        )
    }
}