package by.bashlikovvv.healthrecoveryassistant.di

import by.bashlikovvv.common.remote.wearable.WearableRemoteDataSource
import by.bashlikovvv.common.repository.RootRepository
import by.bashlikovvv.common.source.FilesLocalDataSource
import by.bashlikovvv.common.source.WearableEventsLocalDataSource
import by.bashlikovvv.common.source.WorkManagerSource
import by.bashlikovvv.common.worker.CustomWorkerFactory
import by.bashlikovvv.common.worker.WearableEventsWorker
import by.bashlikovvv.common.worker.WorkerFactoryProvider
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module {
    single {
        WearableEventsLocalDataSource(
            appDispatchers = get(),
            wearableEventsDao = get(),
            wearableTasksDao = get(),
        )
    }

    single { WearableRemoteDataSource() }

    single {
        FilesLocalDataSource(
            appDispatchers = get(),
            contentResolver = androidContext().contentResolver,
        )
    }

    single<WearableEventsWorker.Factory> {
        WearableEventsWorker.Factory.Base(
            wearableEventsLocalDataSource = get(),
            wearableRemoteDataSource = get(),
        )
    }

    single {
        CustomWorkerFactory(
            wearableEventsWorkerFactory = get(),
        )
    }

    factory<WorkerFactoryProvider> {
        val customWorkerFactory: CustomWorkerFactory = get()
        object : WorkerFactoryProvider {
            override fun provideFactory() = customWorkerFactory
        }
    }

    single {
        WorkManagerSource(workerFactoryProvider = get())
    }

    single {
        RootRepository(
            wearableRemoteDataSource = get(),
            filesLocalDataSource = get(),
        )
    }
}