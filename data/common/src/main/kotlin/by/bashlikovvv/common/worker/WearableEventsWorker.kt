package by.bashlikovvv.common.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import by.bashlikovvv.common.remote.wearable.WearableRemoteDataSource
import by.bashlikovvv.common.source.WearableEventsLocalDataSource
import by.bashlikovvv.domain.base.BaseResult
import by.bashlikovvv.domain.model.WearableEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class WearableEventsWorker(
    appContext: Context,
    params: WorkerParameters,
    private val wearableLocalDataSource: WearableEventsLocalDataSource,
    private val wearableRemoteDataSource: WearableRemoteDataSource,
) : CoroutineWorker(appContext, params) {
    init {
        wearableRemoteDataSource.initialize(appContext)
    }

    override suspend fun doWork(): Result {
        when(val result = wearableLocalDataSource.getLatestWearableEvent()) {
            is BaseResult.Success -> result.data?.let { dispatchEvent(it) }
            is BaseResult.Failure -> Unit
        }
        wearableRemoteDataSource.destroy()
        return Result.success()
    }

    suspend fun dispatchEvent(wearableEvent: WearableEvent) {
        Log.i("MUTAG", "executed event: $wearableEvent")
        with(wearableEvent) {
            vibrationDescriptor.actions.forEach { action ->
                vibrate(
                    duration = action.duration,
                    amplitude = action.amplitude,
                )
                delay(action.duration + 500)
            }
            notificationText?.let { notificationTextNotNull ->
                showNotification(notificationTextNotNull)
            }
        }
    }

    private suspend fun vibrate(
        duration: Long,
        amplitude: UByte,
    ) {
        wearableRemoteDataSource.putData(
            path = Vibrate.PATH,
            requestBuilder = {
                putLong(Vibrate.Keys.DURATION, duration)
                putUByte(Vibrate.Keys.AMPLITUDE, amplitude)
            }
        )
    }

    private suspend fun showNotification(text: String) {
        wearableRemoteDataSource.putData(
            path = Notification.PATH,
            requestBuilder = {
                putString(Notification.Keys.TEXT, text)
            }
        )
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