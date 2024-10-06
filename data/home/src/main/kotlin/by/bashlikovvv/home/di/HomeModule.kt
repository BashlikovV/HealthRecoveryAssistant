package by.bashlikovvv.home.di

import by.bashlikovvv.home.repository.HomeRepository
import org.koin.dsl.module

val homeModule = module {
    single {
        HomeRepository(
            wearableEventsLocalDataSource = get(),
            filesLocalDataSource = get(),
            workManagerSource = get(),
        )
    }
}