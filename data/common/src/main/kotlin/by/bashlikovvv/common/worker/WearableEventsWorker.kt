package by.bashlikovvv.common.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import by.bashlikovvv.common.remote.wearable.WearableRemoteDataSource
import by.bashlikovvv.common.local.WearableEventsLocalDataSource
import by.bashlikovvv.domain.base.BaseResult
import by.bashlikovvv.domain.model.WearableEvent
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit

class WearableEventsWorker(
    params: WorkerParameters,
    private val appContext: Context,
    private val wearableLocalDataSource: WearableEventsLocalDataSource,
    private val wearableRemoteDataSource: WearableRemoteDataSource,
) : CoroutineWorker(appContext, params) {
    private val workTag = params.tags.last()

    override suspend fun doWork(): Result {
        wearableRemoteDataSource.initialize(appContext)
        when (val result = wearableLocalDataSource.getLatestWearableEvent()) {
            is BaseResult.Success -> {
                result.data?.let { dispatchEvent(it) }
                wearableLocalDataSource.removeLatestEvent()
            }

            is BaseResult.Failure -> Unit
        }
        rescheduleWork()
        wearableRemoteDataSource.destroy()
        return Result.success()
    }

    private suspend fun rescheduleWork() {
        when (val result = wearableLocalDataSource.getLatestWearableEvent()) {
            is BaseResult.Success -> {
                result.data?.let { wearableEventNotNull ->
                    WorkManager.getInstance(appContext).enqueue(
                            OneTimeWorkRequestBuilder<WearableEventsWorker>().setInitialDelay(
                                    wearableEventNotNull.scheduledTime - System.currentTimeMillis(),
                                    TimeUnit.MILLISECONDS
                                ).addTag(workTag).build()
                        )
                }
            }

            is BaseResult.Failure -> Unit
        }
    }

    private suspend fun dispatchEvent(wearableEvent: WearableEvent) {
        with(wearableEvent) {
            repeat(vibrationDescriptor.repeat.toInt()) {
                for (i in 0..<vibrationDescriptor.onOffSequence.size step 2) {
                    val vibrationDuration = vibrationDescriptor.onOffSequence[i]
                    val delayDuration = vibrationDescriptor.onOffSequence[i + 1]
                    wearableRemoteDataSource.vibrate(
                        duration = vibrationDuration.toLong(),
                        amplitude = 254U
                    )
                    delay((vibrationDuration + delayDuration).toLong())
                }
            }
            notificationText.let { notificationTextNotNull ->
                wearableRemoteDataSource.showNotification(notificationTextNotNull)
            }
        }
    }

    companion object {
        object Vibrate {
            const val PATH = "/vibrate"

            object Keys {
                const val DURATION = "DURATION_KEY"
                const val AMPLITUDE = "AMPLITUDE_KEY"
            }
        }

        object Notification {
            const val PATH = "/notification"

            object Keys {
                const val TEXT = "TEXT_KEY"
            }
        }
    }

    interface Factory {
        fun create(appContext: Context, params: WorkerParameters): WearableEventsWorker

        class Base(
            private val wearableEventsLocalDataSource: WearableEventsLocalDataSource,
            private val wearableRemoteDataSource: WearableRemoteDataSource,
        ) : Factory {
            override fun create(
                appContext: Context,
                params: WorkerParameters,
            ): WearableEventsWorker {
                return WearableEventsWorker(
                    appContext = appContext,
                    params = params,
                    wearableLocalDataSource = wearableEventsLocalDataSource,
                    wearableRemoteDataSource = wearableRemoteDataSource,
                )
            }
        }
    }
}