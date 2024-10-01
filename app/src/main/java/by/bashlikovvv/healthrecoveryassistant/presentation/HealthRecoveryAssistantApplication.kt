package by.bashlikovvv.healthrecoveryassistant.presentation

import android.app.Application
import by.bashlikovvv.healthrecoveryassistant.di.coreModule
import by.bashlikovvv.healthrecoveryassistant.di.dataModule
import by.bashlikovvv.home.di.rootModule
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
                dataModule,
                rootModule,
            )
        }
    }
}