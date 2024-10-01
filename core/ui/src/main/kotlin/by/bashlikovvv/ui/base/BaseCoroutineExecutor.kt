package by.bashlikovvv.ui.base

import by.bashlikovvv.ui.coroutines.CoroutineBlock
import by.bashlikovvv.ui.coroutines.CoroutineManager
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

abstract class BaseCoroutineExecutor<in Intent : Any, in Action : Any, in State : Any, Message : Any, Label : Any>
    : CoroutineExecutor<Intent, Action, State, Message, Label>(), KoinComponent {
    private val coroutineManager: CoroutineManager by inject()

    fun launchIO(
        safeAction: CoroutineBlock,
        onError: (Throwable) -> Unit,
    ): Job = coroutineManager.launchIO(
        scope = scope,
        block = safeAction,
        onError = onError
    )

    fun launchMain(
        safeAction: CoroutineBlock,
        onError: (Throwable) -> Unit,
    ): Job = coroutineManager.launchMain(
        scope = scope,
        block = safeAction,
        onError = onError
    )

    fun launchCustom(
        safeAction: CoroutineBlock,
        customDispatcher: CoroutineDispatcher,
        onError: (Throwable) -> Unit,
    ): Job = coroutineManager.launchCustom(
        scope = scope,
        block = safeAction,
        customDispatcher = customDispatcher,
        onError = onError
    )
}