package by.bashlikovvv.home.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import by.bashlikovvv.ui.res.AppRes

@Composable
internal fun HomeTopBar(
    isInSelectionMode: Boolean,
    modifier: Modifier = Modifier,
    onLoadFile: () -> Unit,
    onSettingsClicked: () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(MaterialTheme.colorScheme.surfaceContainer),
        contentAlignment = Alignment.Center,
    ) {
        if (isInSelectionMode) {
            Button(
                onClick = onLoadFile,
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Text("Cancel selection")
            }
        } else {
            Icon(
                imageVector = AppRes.icons.icSettings,
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(
                        top = 5.dp,
                        end = 5.dp,
                    )
                    .clip(CircleShape)
                    .clickable(onClick = onSettingsClicked)
                    .padding(5.dp)
            )
        }
    }
}