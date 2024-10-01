package by.bashlikovvv.healthrecoveryassistant.di

import by.bashlikovvv.common.remote.wearable.WearableRemoteDataSource
import org.koin.dsl.module

val dataModule = module {
    single { WearableRemoteDataSource() }
}