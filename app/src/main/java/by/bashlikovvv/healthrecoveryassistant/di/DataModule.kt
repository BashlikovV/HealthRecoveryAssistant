package by.bashlikovvv.healthrecoveryassistant.di

import by.bashlikovvv.common.local.ConnectedDevicesLocalDataSource
import by.bashlikovvv.common.local.CurrentDeviceLocalDataStore
import by.bashlikovvv.common.local.FilesLocalDataSource
import by.bashlikovvv.common.local.WearableEventsLocalDataSource
import by.bashlikovvv.common.remote.wearable.WearableRemoteDataSource
import by.bashlikovvv.common.repository.BluetoothRepository
import by.bashlikovvv.common.repository.HARFilesRepository
import by.bashlikovvv.common.repository.RootRepository
import by.bashlikovvv.common.repository.WearableRepository
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

    single<CurrentDeviceLocalDataStore> {
        CurrentDeviceLocalDataStore(
            context = androidContext(),
            appDispatchers = get(),
            devicesLocalDataSource = get(),
        )
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
        WearableRepository(
            wearableEventsLocalDataSource = get(),
        )
    }
}