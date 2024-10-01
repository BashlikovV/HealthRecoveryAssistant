package by.bashlikovvv.home.di

import by.bashlikovvv.home.data.remote.RootRepository
import org.koin.dsl.module

val rootModule = module {
    single {
        RootRepository(
            appDispatchers = get(),
            wearableRemoteDataSource = get()
        )
    }
}