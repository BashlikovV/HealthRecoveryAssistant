package by.bashlikovvv.healthrecoveryassistant.di

import by.bashlikovvv.common.remote.wearable.WearableRemoteDataSource
import by.bashlikovvv.common.repository.WearableRepository
import org.koin.dsl.module

val dataModule = module {
    single { WearableRemoteDataSource() }

    single {
        WearableRepository(
            wearableRemoteDataSource = get()
        )
    }
}