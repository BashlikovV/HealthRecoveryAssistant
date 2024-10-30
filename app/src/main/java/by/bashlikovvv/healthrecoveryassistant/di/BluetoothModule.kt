package by.bashlikovvv.healthrecoveryassistant.di

import by.bashlikovvv.bluetooth.model.QueueEntitiesProvider
import by.bashlikovvv.bluetooth.service.QueueEntitiesProviderImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val bluetoothModule = module {
    factory<QueueEntitiesProvider> {
        QueueEntitiesProviderImpl(
            context = androidContext()
        )
    }
}