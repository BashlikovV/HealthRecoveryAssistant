package by.bashlikovvv.healthrecoveryassistant.di

import by.bashlikovvv.domain.base.AppDispatchers
import by.bashlikovvv.ui.coroutines.CoroutineManager
import by.bashlikovvv.ui.coroutines.CoroutineManagerImpl
import org.koin.dsl.module

val coreModule = module {
    single { AppDispatchers() }

    single<CoroutineManager> {
        val appDispatchers: AppDispatchers = get()

        CoroutineManagerImpl(
            backgroundDispatcher = appDispatchers.io,
            uiDispatcher = appDispatchers.main
        )
    }
}