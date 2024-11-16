package by.bashlikovvv.root.presentation.ui

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import by.bashlikovvv.common.worker.ForegroundService
import by.bashlikovvv.devicesettings.presentation.ui.DeviceSettingsContent
import by.bashlikovvv.discovery.presentation.ui.DiscoveryContent
import by.bashlikovvv.home.presentation.ui.HomeContent
import by.bashlikovvv.common.worker.ForegroundServiceContract
import by.bashlikovvv.root.presentation.ui.component.RootComponent
import by.bashlikovvv.root.presentation.ui.store.RootStore
import by.bashlikovvv.ui.composable.ScreenContent
import by.bashlikovvv.ui.theme.HealthRecoveryAssistantTheme
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation

@Composable
fun RootContent(
    component: RootComponent,
    modifier: Modifier = Modifier,
) {
    ScreenContent(
        contractProvider = component.store,
        initialState = RootStore.State()
    ) { state, label ->
        LabelProcessionBlock(
            label = label,
            foregroundServiceContract = component.foregroundServiceContract,
            onPermissionResult = { permission, granted ->
                dispatchIntent(RootStore.Intent.OnPermissionResult(permission, granted))
            }
        )
        val context = LocalContext.current
        DisposableEffect(Unit) {
            onDispose { component.foregroundServiceContract.unbind(context) }
        }
        HealthRecoveryAssistantTheme(
            languageUiType = state.languageUiType
        ) {
            Surface(
                modifier = modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.systemBars),
            ) {
                Children(
                    stack = component.stack,
                    modifier = Modifier.fillMaxSize(),
                    animation = stackAnimation()
                ) {
                    when(val instance = it.instance) {
                        is RootComponent.Child.Home -> HomeContent(instance.component)
                        is RootComponent.Child.Discovery -> DiscoveryContent(instance.component)
                        is RootComponent.Child.DeviceSettings -> DeviceSettingsContent(instance.component)
                    }
                }
            }
        }
    }
}

@Composable
private fun LabelProcessionBlock(
    label: RootStore.Label?,
    foregroundServiceContract: ForegroundServiceContract,
    onPermissionResult: (String, Boolean) -> Unit,
) {
    val requestPermissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { it.forEach { (permission, granted) -> onPermissionResult(permission, granted) } }
    val context = LocalContext.current
    LaunchedEffect(label) {
        when(label) {
            is RootStore.Label.OpenHARFile -> Unit
            is RootStore.Label.RequestPermission -> requestPermissionsLauncher.launch(
                arrayOf(label.permission)
            )
            is RootStore.Label.StartForegroundService -> {
                Intent(context, ForegroundService::class.java).also { intent ->
                    context.startService(intent)
                }
                foregroundServiceContract.bind(context)
            }
            else -> Unit
        }
    }
}