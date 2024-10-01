package by.bashlikovvv.ui.coroutines

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class CoroutineManagerImpl(
    override val backgroundDispatcher: CoroutineDispatcher,
    override val uiDispatcher: CoroutineDispatcher,
) : CoroutineManager {
    override fun launchIO(
        scope: CoroutineScope,
        block: CoroutineBlock,
        onError: (Throwable) -> Unit,
    ): Job {
        val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
            scope.launch(uiDispatcher) {
                onError(throwable)
            }
        }

        return scope.launch(
            context = exceptionHandler + backgroundDispatcher,
            block = block
        )
    }

    override fun launchMain(
        scope: CoroutineScope,
        block: CoroutineBlock,
        onError: (Throwable) -> Unit,
    ): Job {
        val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
            scope.launch(uiDispatcher) {
                onError(throwable)
            }
        }

        return scope.launch(
            context = exceptionHandler + uiDispatcher,
            block = block
        )
    }

    override fun launchCustom(
        scope: CoroutineScope,
        block: CoroutineBlock,
        customDispatcher: CoroutineDispatcher,
        onError: (Throwable) -> Unit,
    ): Job {
        val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
            scope.launch(uiDispatcher) {
                onError(throwable)
            }
        }

        return scope.launch(
            context = exceptionHandler + customDispatcher,
            block = block
        )
    }
}