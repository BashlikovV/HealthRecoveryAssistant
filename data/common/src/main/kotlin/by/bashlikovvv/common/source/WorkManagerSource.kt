package by.bashlikovvv.common.source

import android.content.Context
import androidx.work.Configuration
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerFactory
import by.bashlikovvv.common.worker.WearableEventsWorker
import java.util.concurrent.TimeUnit

class WorkManagerSource(
    private val workerFactoryProvider: WorkerFactoryProvider,
) {
    fun initializeWorkManager(
        context: Context,
        provider: WorkerFactoryProvider = workerFactoryProvider,
    ) {
        val factory = provider.provideFactory()
        val workManagerConfig = Configuration.Builder()
            .let { if (factory != null ) it.setWorkerFactory(factory) else it }
            .build()
        WorkManager.initialize(context, workManagerConfig)
    }

    fun enqueueUniqueWork(
        context: Context,
        delay: Long
    ) {
        WorkManager.getInstance(context)
            .apply {
                cancelAllWorkByTag(UNIQUE_WORK_TAG)
                enqueue(
                    OneTimeWorkRequestBuilder<WearableEventsWorker>()
                        .addTag(UNIQUE_WORK_TAG)
                        .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                        .build()
                )
            }
    }

    companion object {
        const val UNIQUE_WORK_TAG = "421babd4-9739-4209-98e6-9c2ef548a664"
    }
}

interface WorkerFactoryProvider {
    fun provideFactory(): WorkerFactory?

    class Base() : WorkerFactoryProvider {
        override fun provideFactory(): WorkerFactory? = null
    }
}