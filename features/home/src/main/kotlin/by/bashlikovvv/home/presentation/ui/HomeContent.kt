package by.bashlikovvv.home.presentation.ui

import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import by.bashlikovvv.domain.model.WearableEvents
import by.bashlikovvv.home.domain.model.DevicesListItems
import by.bashlikovvv.home.presentation.ui.component.HomeComponent
import by.bashlikovvv.home.presentation.ui.store.HomeStore
import by.bashlikovvv.ui.composable.ScreenContent
import by.bashlikovvv.ui.theme.HealthRecoveryAssistantTheme

@Composable
fun HomeContent(
    component: HomeComponent,
    modifier: Modifier = Modifier,
) {
    ScreenContent(
        contractProvider = component.store,
        initialState = HomeStore.State()
    ) { state, label ->
        val startActivityForeResultLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) { dispatchIntent(HomeStore.Intent.OnActivityResult(it)) }
        val context = LocalContext.current
        var sequence by remember { mutableIntStateOf(0) }
        HomeScreenContent(
            state = state,
            modifier = modifier,
            onLoadFile = { startActivityForeResultLauncher.launchFilesPicker() },
            onFABClicked = component::startDiscoveringNewDevices,
            onDeviceClicked = { dispatchIntent(HomeStore.Intent.DeviceClick(it)) },
            onVibrate = { dispatchIntent(HomeStore.Intent.Vibrate) },
            onScheduleNotifications = {
                dispatchIntent(HomeStore.Intent.ScheduleFileData(WearableEvents(listOf()), context))
            },
            onSetVibrationProfile = {
                dispatchIntent(HomeStore.Intent.SetVibrationProfile(intArrayOf(100, 0) to 5))
            }
        )
    }
}

@[Composable OptIn(ExperimentalMaterial3Api::class)]
private fun HomeScreenContent(
    state: HomeStore.State,
    modifier: Modifier = Modifier,
    onLoadFile: () -> Unit,
    onFABClicked: () -> Unit,
    onDeviceClicked: (DevicesListItems.Device) -> Unit,
    onVibrate: () -> Unit,
    onScheduleNotifications: () -> Unit,
    onSetVibrationProfile: () -> Unit,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            HomeTopBar(
                isInSelectionMode = state.isInSelectionMode,
                onLoadFile = onLoadFile
            )
        },
        floatingActionButton = {
            HomeFloatingActionButton(
                onCLick = onFABClicked
            )
        }
    ) { paddingValues ->
        Column(modifier.padding(paddingValues)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Button(onClick = onSetVibrationProfile) {
                    Text("set vibration profile")
                }
            }
            DevicesList(
                list = state.devicesList,
                modifier = Modifier.fillMaxSize(),
                onDeviceClicked = { onDeviceClicked(it) },
                onVibrate = onVibrate,
                onScheduleNotifications = onScheduleNotifications,
            )
        }
    }
}

private fun  ManagedActivityResultLauncher<Intent, ActivityResult>.launchFilesPicker() {
    val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
        type = "*/*"
    }
    val chooser = Intent.createChooser(intent, "Choose a file")
    launch(chooser)
}

@[Composable Preview]
private fun Preview() {
    HealthRecoveryAssistantTheme {
        HomeScreenContent(
            state = HomeStore.State(),
            onLoadFile = {},
            onFABClicked = {},
            onDeviceClicked = {},
            onVibrate = {},
            onScheduleNotifications = {},
            onSetVibrationProfile = {}
        )
    }
}