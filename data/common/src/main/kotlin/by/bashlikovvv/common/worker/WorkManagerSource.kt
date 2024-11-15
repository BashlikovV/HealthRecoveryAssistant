package by.bashlikovvv.common.worker

import android.content.Context
import androidx.work.Configuration
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.Worker
import by.bashlikovvv.domain.model.BluetoothDevice
import by.bashlikovvv.domain.model.BluetoothDeviceType
import kotlin.reflect.KClass

class WorkManagerSource(
    private val workerFactoryProvider: WorkerFactoryProvider = WorkerFactoryProvider.Base,
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
        device: BluetoothDevice,
        context: Context,
        delay: Long
    ) {
        getWorkerByDeviceType(device.type)?.let { worker ->
            WorkManager.getInstance(context)
                .apply {
                    cancelAllWorkByTag(UNIQUE_WORK_TAG)
                    val workRequest = createWorkRequest(worker, device)
                    enqueue(workRequest)
                }
        }
    }

    private fun createWorkRequest(
        worker: KClass<out Worker>,
        device: BluetoothDevice,
    ): OneTimeWorkRequest = OneTimeWorkRequest.Builder(worker)
        .setInputData(
            Data.Builder()
                .putString(KEY_DEVICE_ADDRESS, device.address)
                .build()
        )
        .addTag(UNIQUE_WORK_TAG)
        .build()

    companion object {
        const val UNIQUE_WORK_TAG = "421babd4-9739-4209-98e6-9c2ef548a664"

        const val KEY_DEVICE_ADDRESS = "KEY_DEVICE_ADDRESS"
    }

    private fun getWorkerByDeviceType(deviceType: BluetoothDeviceType): KClass<out Worker>? {
        return when(deviceType) {
            BluetoothDeviceType.Unknown -> null
            BluetoothDeviceType.MiBand5 -> (MiBand5Worker::class as KClass<out Worker>)
        }
    }
}