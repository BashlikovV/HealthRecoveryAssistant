package by.bashlikovvv.discovery.presentation.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import by.bashlikovvv.discovery.domain.model.DiscoveryListItems

@Composable
internal fun DevicesList(
    list: List<DiscoveryListItems>,
    modifier: Modifier = Modifier,
    onDeviceClicked: (String) -> Unit,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        items(
            items = list,
            key = { item -> item.id },
            contentType = { item -> item.javaClass },
        ) { item ->
            when(item) {
                is DiscoveryListItems.Device -> DevicesListDeviceItem(
                    name = item.name,
                    address = item.address,
                    onItemCLicked = { onDeviceClicked(item.address) }
                )
                is DiscoveryListItems.Progress -> DevicesListProgressItem()
            }
        }
    }
}

@Composable
private fun DevicesListProgressItem(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val infiniteTransition = rememberInfiniteTransition()
        val progress = infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    delayMillis = 250,
                    easing = LinearEasing,
                    durationMillis = 1000,
                ),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "Progress"
        )
        LinearProgressIndicator(
            progress = { progress.value },
            modifier = Modifier
                .padding(horizontal = 15.dp)
                .fillMaxWidth()
                .height(4.dp),
            drawStopIndicator = {  },
        )
    }
}

@Composable
private fun DevicesListDeviceItem(
    name: String,
    address: String,
    modifier: Modifier = Modifier,
    onItemCLicked: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .clickable(onClick = onItemCLicked)
            .padding(15.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(name)
        Text(address)
    }
}