package by.bashlikovvv.home.presentation.ui

import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import by.bashlikovvv.ui.res.AppRes

@Composable
internal fun HomeFloatingActionButton(
    modifier: Modifier = Modifier,
    onCLick: () -> Unit,
) {
    FloatingActionButton(
        onClick = onCLick,
        modifier = modifier,
    ) {
        Icon(
            imageVector = AppRes.icons.icAdd,
            contentDescription = AppRes.strings.startDiscoveringNewDevices,
        )
    }
}