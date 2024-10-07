package by.bashlikovvv.common.worker

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters

class CustomWorkerFactory(
    private val wearableEventsWorkerFactory: WearableEventsWorker.Factory,
) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters,
    ): ListenableWorker? {
        return when(workerClassName) {
            WearableEventsWorker::class.java.name ->
                wearableEventsWorkerFactory.create(appContext, workerParameters)
            else -> null
        }
    }
}