package by.bashlikovvv.healthrecoveryassistant.di

import android.content.Context
import androidx.core.app.ActivityCompat
import by.bashlikovvv.domain.base.AppDispatchers
import by.bashlikovvv.domain.base.SystemServiceProvider
import by.bashlikovvv.domain.model.BluetoothService
import by.bashlikovvv.domain.source.BluetoothServiceImpl
import by.bashlikovvv.ui.coroutines.CoroutineManager
import by.bashlikovvv.ui.coroutines.CoroutineManagerImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val coreModule = module {
    single { AppDispatchers() }

    single<CoroutineManager> {
        val appDispatchers: AppDispatchers = get()

        CoroutineManagerImpl(
            backgroundDispatcher = appDispatchers.io,
            uiDispatcher = appDispatchers.main
        )
    }

    single<SystemServiceProvider> {
        val context: Context = androidContext()
        object : SystemServiceProvider {
            override fun getSystemService(name: String): Any? {
                return context.getSystemService(name)
            }
        }
    }

    single<BluetoothService> {
        BluetoothServiceImpl(
            systemServiceProvider = get(),
            checkSelfPermission = { permission ->
                ActivityCompat.checkSelfPermission(androidContext(), permission)
            },
        )
    }
}