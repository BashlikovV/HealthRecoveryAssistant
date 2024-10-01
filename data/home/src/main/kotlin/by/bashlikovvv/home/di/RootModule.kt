package by.bashlikovvv.home.di

import by.bashlikovvv.home.data.remote.HomeRepository
import org.koin.dsl.module

val rootModule = module {
    single {
        HomeRepository(
            appDispatchers = get(),
            wearableRemoteDataSource = get()
        )
    }
}