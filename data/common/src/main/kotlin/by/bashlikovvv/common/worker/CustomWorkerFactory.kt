package by.bashlikovvv.common.worker

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters

class CustomWorkerFactory(
    private val wearableEventsWorkerFactory: WearableEventsWorker.Factory,
    private val miBand5WorkerFactory: MiBand5Worker.Factory,
) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters,
    ): ListenableWorker? {
        return when(workerClassName) {
            WearableEventsWorker::class.java.name ->
                wearableEventsWorkerFactory.create(appContext, workerParameters)
            MiBand5Worker::class.java.name ->
                miBand5WorkerFactory.create(appContext, workerParameters)

            else -> null
        }
    }
}