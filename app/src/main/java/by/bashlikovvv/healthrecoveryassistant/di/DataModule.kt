package by.bashlikovvv.healthrecoveryassistant.di

import androidx.room.Room
import by.bashlikovvv.common.local.HRADatabase
import by.bashlikovvv.common.local.contract.HRADRoomContract
import by.bashlikovvv.common.remote.wearable.WearableRemoteDataSource
import by.bashlikovvv.common.repository.WearableRepository
import by.bashlikovvv.common.source.FilesLocalDataSource
import by.bashlikovvv.common.source.WearableEventsLocalDataSource
import by.bashlikovvv.common.source.WorkManagerSource
import by.bashlikovvv.common.source.WorkerFactoryProvider
import by.bashlikovvv.common.worker.CustomWorkerFactory
import by.bashlikovvv.common.worker.WearableEventsWorker
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module {
    single {
        Room.databaseBuilder(
            context = androidContext(),
            klass = HRADatabase::class.java,
            name = HRADRoomContract.DATABASE_NAME
        ).build()
    }

    single {
        val database: HRADatabase = get()
        database.wearableTasksDao
    }

    single {
        val database: HRADatabase = get()
        database.wearableEventsDao
    }

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
        WearableRepository(
            wearableRemoteDataSource = get()
        )
    }
}