package by.bashlikovvv.home.presentation.ui

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
import androidx.compose.ui.unit.dp
import by.bashlikovvv.home.presentation.ui.component.HomeComponent
import by.bashlikovvv.home.presentation.ui.store.HomeStore
import by.bashlikovvv.ui.composable.ScreenContent

@Composable
fun HomeContent(
    component: HomeComponent,
    modifier: Modifier = Modifier,
) {
    ScreenContent(
        contractProvider = component.store,
        initialState = HomeStore.State()
    ) { state, _ ->
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
                    Text("duration: ")
                    TextField(
                        value = durationText,
                        onValueChange = { durationText = it },
                        placeholder = { Text("duration time in long") },
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
                    Text("amplitude: ")
                    TextField(
                        value = amplitudeText,
                        onValueChange = { amplitudeText = it },
                        placeholder = { Text("amplitude in int (max 255)") },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        )
                    )
                }
                androidx.compose.material3.Button(
                    onClick = {
                        dispatchIntent(
                            HomeStore.Intent.Vibrate(
                                durationText.toLong(),
                                amplitudeText.toInt()
                            )
                        )
                    }
                ) {
                    Text("send vibrate")
                }
            }
        }
    }
}