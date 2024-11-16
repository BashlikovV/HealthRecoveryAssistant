package by.bashlikovvv.healthrecoveryassistant.di

import by.bashlikovvv.common.local.ConnectedDevicesLocalDataSource
import by.bashlikovvv.common.local.FilesLocalDataSource
import by.bashlikovvv.common.local.WearableEventsLocalDataSource
import by.bashlikovvv.common.remote.wearable.WearableRemoteDataSource
import by.bashlikovvv.common.repository.BluetoothRepository
import by.bashlikovvv.common.repository.HARFilesRepository
import by.bashlikovvv.common.repository.RootRepository
import by.bashlikovvv.common.repository.WearableRepository
import by.bashlikovvv.common.worker.CustomWorkerFactory
import by.bashlikovvv.common.worker.MiBand5Worker
import by.bashlikovvv.common.worker.WearableEventsWorker
import by.bashlikovvv.common.worker.WorkManagerSource
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

    single<MiBand5Worker.Factory> {
        MiBand5Worker.Factory.Base(
            wearableLocalDataSource = get(),
            bluetoothRepository = get(),
        )
    }

    single {
        CustomWorkerFactory(
            wearableEventsWorkerFactory = get(),
            miBand5WorkerFactory = get(),
        )
    }

    factory<WorkerFactoryProvider> {
        val customWorkerFactory: CustomWorkerFactory = get()
        object : WorkerFactoryProvider {
            override fun provideFactory() = customWorkerFactory
        }
    }

    single {
        ConnectedDevicesLocalDataSource(
            appDispatchers = get(),
            connectedDevicesDao = get(),
        )
    }

    single {
        RootRepository(
            wearableRemoteDataSource = get(),
        )
    }

    single {
        HARFilesRepository(
            filesLocalDataSource = get(),
        )
    }

    single {
        BluetoothRepository(
            bluetoothService = get(),
            queueEntitiesProvider = get(),
            connectedDevicesLocalDataSource = get(),
            appDispatchers = get(),
        )
    }

    single {
        WorkManagerSource(
            workerFactoryProvider = get(),
        )
    }

    single {
        WearableRepository(
            wearableEventsLocalDataSource = get(),
            workManagerSource = get(),
        )
    }
}