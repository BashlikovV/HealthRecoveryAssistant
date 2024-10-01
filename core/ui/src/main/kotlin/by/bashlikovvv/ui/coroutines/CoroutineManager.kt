package by.bashlikovvv.ui.coroutines

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

typealias CoroutineBlock = suspend CoroutineScope.() -> Unit

interface CoroutineManager {
    val backgroundDispatcher: CoroutineDispatcher
    val uiDispatcher: CoroutineDispatcher

    fun launchIO(
        scope: CoroutineScope,
        block: CoroutineBlock,
        onError: (Throwable) -> Unit,
    ): Job

    fun launchMain(
        scope: CoroutineScope,
        block: CoroutineBlock,
        onError: (Throwable) -> Unit,
    ): Job

    fun launchCustom(
        scope: CoroutineScope,
        block: CoroutineBlock,
        customDispatcher: CoroutineDispatcher,
        onError: (Throwable) -> Unit,
    ): Job
}