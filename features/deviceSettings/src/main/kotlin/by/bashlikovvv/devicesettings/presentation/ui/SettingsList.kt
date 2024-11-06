package by.bashlikovvv.devicesettings.presentation.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import by.bashlikovvv.devicesettings.domain.model.NotificationType
import by.bashlikovvv.devicesettings.domain.model.SettingsListItems
import by.bashlikovvv.devicesettings.domain.model.VibrationProfile
import kotlinx.collections.immutable.ImmutableList

@Composable
internal fun SettingsList(
    list: ImmutableList<SettingsListItems>,
    modifier: Modifier = Modifier,
    onSetKey: (String) -> Unit,
    onVibrationProfile: (test: Boolean) -> Unit,
    onChooseNotificationType: () -> Unit,
    onChooseVibrationProfile: () -> Unit,
) {
    val itemModifier = Modifier
        .clip(RoundedCornerShape(15.dp))
        .border(
            width = 1.dp,
            color = Color.White,
            shape = RoundedCornerShape(15.dp)
        )
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        items(
            items = list,
            key = { it.id },
            contentType = { it::class }
        ) { item ->
            when(item) {
                is SettingsListItems.ItemKeySettings -> ItemKeySettings(
                    key = item.key,
                    modifier = itemModifier,
                    onSetKey = onSetKey,
                )
                is SettingsListItems.ItemVibrationProfileSettings -> ItemVibrationProfileSettings(
                    notificationType = item.notificationType,
                    vibrationProfile = item.vibrationProfile,
                    modifier = itemModifier,
                    onVibrationProfile = onVibrationProfile,
                    onChooseNotificationType = onChooseNotificationType,
                    onChooseVibrationProfile = onChooseVibrationProfile,
                )
            }
        }
    }
}

@Composable
private fun ItemKeySettings(
    key: String,
    modifier: Modifier = Modifier,
    onSetKey: (String) -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Text("Set auth key")
        var keyValue by remember { mutableStateOf(key) }
        OutlinedTextField(
            value = keyValue,
            onValueChange = { keyValue = it },
            modifier = Modifier.fillMaxWidth(),
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Button(
                onClick = { keyValue = key },
                modifier = Modifier.weight(0.5f),
            ) {
                Text("Restore")
            }
            Button(
                onClick = { onSetKey(keyValue) },
                modifier = Modifier.weight(0.5f)
            ) {
                Text("Save")
            }
        }
    }
}

@Composable
private fun ItemVibrationProfileSettings(
    notificationType: NotificationType,
    vibrationProfile: VibrationProfile,
    modifier: Modifier = Modifier,
    onVibrationProfile: (test: Boolean) -> Unit,
    onChooseNotificationType: () -> Unit,
    onChooseVibrationProfile: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                notificationType.name,
                modifier = Modifier,
                textAlign = TextAlign.Center,
            )

            Button(onClick = onChooseNotificationType) {
                Text("Change")
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                vibrationProfile.name,
                modifier = Modifier,
                textAlign = TextAlign.Center,
            )

            Button(onClick = onChooseVibrationProfile) {
                Text("Change")
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { onVibrationProfile(true) },
                modifier = Modifier.weight(0.5f)
            ) {
                Text("Test")
            }

            Button(
                onClick = { onVibrationProfile(false) },
                modifier = Modifier.weight(0.5f)
            ) {
                Text("Set")
            }
        }
    }
}