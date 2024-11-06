package by.bashlikovvv.devicesettings.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import by.bashlikovvv.devicesettings.domain.model.VibrationProfile
import by.bashlikovvv.ui.dialog.AlertDialogScope

@Composable
fun AlertDialogScope.VibrationProfileDialogContent(
    vibrationProfile: VibrationProfile,
    modifier: Modifier = Modifier,
    onConfirm: (VibrationProfile) -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(RoundedCornerShape(15.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        val name = vibrationProfile.name
        var selectedItem by remember { mutableStateOf(name) }
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            items(VibrationProfile.entries) { item ->
                ItemVibrationProfile(
                    item = item,
                    isSelected = item == selectedItem,
                    onCLicked = { selectedItem = item }
                )
            }
        }
        Button(
            onClick = {
                onConfirm(VibrationProfile.byName(selectedItem))
                dismiss()
            }
        ) {
            Text("Confirm")
        }
    }
}

@Composable
private fun ItemVibrationProfile(
    item: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onCLicked: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable(onClick = onCLicked),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(item)

        Checkbox(checked = isSelected, onCheckedChange = null)
    }
}