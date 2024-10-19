package by.bashlikovvv.ui.base

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory

abstract class BaseStoreFactory<out T : Store<*, *, *>>(
    protected val storeFactory: StoreFactory,
) {
    abstract fun create(): T
}