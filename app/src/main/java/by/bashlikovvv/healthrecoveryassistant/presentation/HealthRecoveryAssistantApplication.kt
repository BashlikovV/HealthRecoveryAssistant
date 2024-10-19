package by.bashlikovvv.healthrecoveryassistant.presentation

import android.app.Application
import by.bashlikovvv.common.worker.WorkManagerSource
import by.bashlikovvv.healthrecoveryassistant.di.coreModule
import by.bashlikovvv.healthrecoveryassistant.di.dataModule
import by.bashlikovvv.healthrecoveryassistant.di.databaseModule
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class HealthRecoveryAssistantApplication : Application() {
    private val workManagerSource: WorkManagerSource by inject()

    override fun onCreate() {
        super.onCreate()
        initDI()
        workManagerSource.initializeWorkManager(this)
    }

    private fun initDI() {
        startKoin {
            androidContext(this@HealthRecoveryAssistantApplication)
            modules(
                coreModule,
                databaseModule,
                dataModule,
            )
        }
    }
}