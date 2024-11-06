package by.bashlikovvv.home.presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import by.bashlikovvv.home.domain.model.DevicesListItems
import by.bashlikovvv.ui.theme.HealthRecoveryAssistantTheme
import kotlinx.collections.immutable.ImmutableList

@Composable
internal fun DevicesList(
    list: ImmutableList<DevicesListItems>,
    modifier: Modifier = Modifier,
    onDeviceClicked: (DevicesListItems.Device) -> Unit,
    onVibrate: () -> Unit,
    onScheduleNotifications: () -> Unit,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        if (list.isEmpty()) {
            Text(text = "No connected devices")
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
            ) {
                items(
                    items = list,
                    key = { it.id },
                    contentType = { it::class }
                ) { item ->
                    when(item) {
                        is DevicesListItems.Device -> ItemDevice(
                            name = item.name,
                            address = item.address,
                            isConnected = item.connected,
                            onClicked = { onDeviceClicked(item) },
                            onVibrate = onVibrate,
                            onScheduleNotifications = onScheduleNotifications,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ItemDevice(
    name: String,
    address: String,
    isConnected: Boolean,
    modifier: Modifier = Modifier,
    onClicked: () -> Unit,
    onVibrate: () -> Unit,
    onScheduleNotifications: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClicked),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                "$name : $address",
                modifier = Modifier.align(Alignment.Center)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(if (isConnected) "connected" else "click to connect")
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Button(
                onClick = onVibrate,
                modifier = Modifier.weight(0.5f)
            ) {
                Text("vibrate")
            }
            Button(
                onClick = onScheduleNotifications,
                modifier = Modifier.weight(0.5f)
            ) {
                Text("schedule")
            }
        }
    }
}

@Composable
@Preview
private fun ItemDevicePreview() {
    HealthRecoveryAssistantTheme {
        ItemDevice(
            name = "test",
            address = "00:00:00:00",
            isConnected = false,
            onVibrate = {},
            onClicked = {},
            onScheduleNotifications = {},
        )
    }
}