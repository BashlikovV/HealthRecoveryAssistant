package by.bashlikovvv.healthrecoveryassistant.presentation

import android.app.Application
import by.bashlikovvv.healthrecoveryassistant.di.bluetoothModule
import by.bashlikovvv.healthrecoveryassistant.di.coreModule
import by.bashlikovvv.healthrecoveryassistant.di.dataModule
import by.bashlikovvv.healthrecoveryassistant.di.databaseModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class HealthRecoveryAssistantApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initDI()
    }

    private fun initDI() {
        startKoin {
            androidContext(this@HealthRecoveryAssistantApplication)
            modules(
                coreModule,
                databaseModule,
                bluetoothModule,
                dataModule,
            )
        }
    }
}