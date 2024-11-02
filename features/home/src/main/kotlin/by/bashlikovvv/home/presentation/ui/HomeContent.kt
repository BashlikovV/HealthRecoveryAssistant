package by.bashlikovvv.home.presentation.ui

import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
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

@[Composable OptIn(ExperimentalMaterial3Api::class)]
private fun HomeScreenContent(
    state: HomeStore.State,
    modifier: Modifier = Modifier,
    onLoadFile: () -> Unit,
    scheduleFileData: () -> Unit,
    onFABClicked: () -> Unit,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            HomeTopBar(
                onLoadFile = onLoadFile
            )
        },
        floatingActionButton = {
            HomeFloatingActionButton(
                onCLick = onFABClicked
            )
        }
    ) { paddingValues ->
        Box(modifier.padding(paddingValues)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {

            }
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
            onLoadFile = { },
            scheduleFileData = { },
            onFABClicked = { }
        )
    }
}