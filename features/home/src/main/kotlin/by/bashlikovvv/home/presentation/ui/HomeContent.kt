package by.bashlikovvv.home.presentation.ui

import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import by.bashlikovvv.home.presentation.ui.component.HomeComponent
import by.bashlikovvv.home.presentation.ui.store.HomeStore
import by.bashlikovvv.ui.composable.ScreenContent
import by.bashlikovvv.ui.res.AppRes
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
        HomeScreenContent(
            state = state,
            modifier = modifier,
            onLoadFile = { startActivityForeResultLauncher.launchFilesPicker() },
            scheduleFileData = {
                state.fileContent?.let {
                    dispatchIntent(HomeStore.Intent.ScheduleFileData(it, context))
                }
            },
            onFABClicked = component::startDiscoveringNewDevices
        )
    }
}

private fun  ManagedActivityResultLauncher<Intent, ActivityResult>.launchFilesPicker() {
    val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
        type = "*/*"
    }
    val chooser = Intent.createChooser(intent, "Choose a file")
    launch(chooser)
}

@[Composable OptIn(ExperimentalMaterial3Api::class)]
private fun HomeScreenContent(
    state: HomeStore.State,
    modifier: Modifier = Modifier,
    onLoadFile: () -> Unit,
    scheduleFileData: () -> Unit,
    onFABClicked: () -> Unit,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Button(onClick = onLoadFile) {
                Text(text = AppRes.strings.loadFile)
            }
            if (state.fileName != null) {
                Button(onClick = scheduleFileData) {
                    Text(text = "${AppRes.strings.scheduleFileData}: ${state.fileName}")
                }
                Text(text = state.fileContent?.events?.joinToString() ?: "null")
            }
        }
        FloatingActionButton(
            onClick = onFABClicked,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(
                    end = 15.dp,
                    bottom = 15.dp
                )
        ) {
            Icon(
                imageVector = AppRes.icons.icAdd,
                contentDescription = AppRes.strings.startDiscoveringNewDevices,
            )
        }
    }
}

@[Composable Preview]
private fun Preview() {
    HealthRecoveryAssistantTheme {
        HomeScreenContent(
            state = HomeStore.State(),
            onLoadFile = { },
            scheduleFileData = { },
            onFABClicked = { }
        )
    }
}