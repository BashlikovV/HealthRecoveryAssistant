package by.bashlikovvv.home.presentation.ui

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
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
    Intent.CATEGORY_BROWSABLE
    ScreenContent(
        contractProvider = component.store,
        initialState = HomeStore.State()
    ) { state, _ ->
        HomeScreenContent(
            state = state,
            modifier = modifier,
            onVibrate = { duration, amplitude ->
                dispatchIntent(HomeStore.Intent.Vibrate(duration, amplitude))
            }
        )
    }
}

@Composable
private fun HomeScreenContent(
    state: HomeStore.State,
    modifier: Modifier = Modifier,
    onVibrate: (Long, Int) -> Unit,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .wrapContentHeight()
                .width(IntrinsicSize.Max),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            var durationText: String by remember { mutableStateOf("") }
            Row(
                modifier = Modifier.wrapContentSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("${AppRes.strings.duration}: ")
                TextField(
                    value = durationText,
                    onValueChange = { durationText = it },
                    placeholder = { Text(AppRes.strings.durationTimeInLong) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )
                )
            }
            var amplitudeText: String by androidx.compose.runtime.remember { mutableStateOf("") }
            Row(
                modifier = Modifier.wrapContentSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("${AppRes.strings.amplitude}: ")
                TextField(
                    value = amplitudeText,
                    onValueChange = { amplitudeText = it },
                    placeholder = { Text(AppRes.strings.amplitudeInIntMax255) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )
                )
            }
            Button(
                onClick = {
                    onVibrate(durationText.toLong(), amplitudeText.toInt())
                }
            ) {
                Text(AppRes.strings.sendVibrate)
            }
        }
    }
}

@[Composable Preview]
private fun Preview() {
    HealthRecoveryAssistantTheme {
        HomeScreenContent(
            state = HomeStore.State(),
            onVibrate = { _, _ -> }
        )
    }
}