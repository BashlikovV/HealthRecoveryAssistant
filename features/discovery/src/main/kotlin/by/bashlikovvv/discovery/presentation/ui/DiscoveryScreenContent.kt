package by.bashlikovvv.discovery.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import by.bashlikovvv.discovery.domain.model.DiscoveryListItems
import by.bashlikovvv.discovery.presentation.ui.store.DiscoveryStore
import by.bashlikovvv.ui.res.AppRes

@Composable
internal fun DiscoveryScreenContent(
    state: DiscoveryStore.State,
    modifier: Modifier = Modifier,
    onDiscoveryClicked: () -> Unit,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Button(
            onClick = onDiscoveryClicked,
            modifier = Modifier.padding(top = 15.dp)
        ) {
            Text(
                text = if (state.isScanning)
                    AppRes.strings.stopDiscoveringDevices
                else
                    AppRes.strings.startDiscoveringNewDevices
            )
        }
        if (state.devices.isNotEmpty()) {
            DevicesList(
                list = if (state.isScanning) {
                    state.devices + listOf(
                        DiscoveryListItems.Progress(state.devices.maxOf { it.id } + 1)
                    )
                } else {
                    state.devices
                },
                onDeviceClicked = { address -> }
            )
        }
    }
}