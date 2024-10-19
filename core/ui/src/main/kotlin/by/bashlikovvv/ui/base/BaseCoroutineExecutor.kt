@file:Suppress("UNUSED")

package by.bashlikovvv.ui.base

import android.util.Log
import by.bashlikovvv.ui.coroutines.CoroutineBlock
import by.bashlikovvv.ui.coroutines.CoroutineManager
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

abstract class BaseCoroutineExecutor<in Intent : Any, in Action : Any, in State : Any, Message : Any, Label : Any> :
    CoroutineExecutor<Intent, Action, State, Message, Label>(), KoinComponent {
    private val coroutineManager: CoroutineManager by inject()

    fun launchIO(
        onError: (Throwable) -> Unit = defaultOnErrorBlock,
        safeAction: CoroutineBlock,
    ): Job = coroutineManager.launchIO(
        scope = scope,
        block = safeAction,
        onError = onError
    )

    fun launchMain(
        onError: (Throwable) -> Unit = defaultOnErrorBlock,
        safeAction: CoroutineBlock,
    ): Job = coroutineManager.launchMain(
        scope = scope,
        block = safeAction,
        onError = onError
    )

    fun launchCustom(
        customDispatcher: CoroutineDispatcher,
        onError: (Throwable) -> Unit = defaultOnErrorBlock,
        safeAction: CoroutineBlock,
    ): Job = coroutineManager.launchCustom(
        scope = scope,
        block = safeAction,
        customDispatcher = customDispatcher,
        onError = onError
    )

    suspend fun dispatchOnMainThread(msg: Message) = withContext(coroutineManager.uiDispatcher) {
        this@BaseCoroutineExecutor.dispatch(msg)
    }

    suspend fun publishOnMainThread(label: Label) = withContext(coroutineManager.uiDispatcher) {
        this@BaseCoroutineExecutor.publish(label)
    }

    companion object {
        private val defaultOnErrorBlock: (Throwable) -> Unit = { th ->
            Log.e("MYTAG", "exception in coroutine block", th)
        }
    }
}