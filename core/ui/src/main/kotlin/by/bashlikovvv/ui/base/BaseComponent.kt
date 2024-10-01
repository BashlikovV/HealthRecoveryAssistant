package by.bashlikovvv.ui.base

import by.bashlikovvv.ui.ext.coroutineScope
import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

abstract class BaseComponent(
    componentContext: ComponentContext
) : ComponentContext by componentContext {
    private val scope by lazy {
        coroutineScope(Dispatchers.Main.immediate + SupervisorJob())
    }

    fun <Label : Any>observeLabels(labels: Flow<Label>, onLabel: (label: Label) -> Unit) {
        labels
            .onEach { onLabel(it) }
            .launchIn(scope)
    }
}