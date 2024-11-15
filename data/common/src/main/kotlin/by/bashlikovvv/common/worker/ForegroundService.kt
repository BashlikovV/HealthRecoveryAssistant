package by.bashlikovvv.common.worker

import android.R
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import by.bashlikovvv.common.local.WearableEventsLocalDataSource
import by.bashlikovvv.domain.base.BaseResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.UUID
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

class ForegroundService : Service(), KoinComponent {
    private val wearableLocalDataSource: WearableEventsLocalDataSource by inject()

    private val notificationManager by lazy {
        getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    }

    private var isInterrupted: Boolean = false

    private val thread = getForegroundServiceThread()

    private val binder = LocalBinder()

    override fun onBind(p0: Intent?): IBinder = binder

    private val queue = LinkedBlockingQueue<UUID>()

    override fun onStart(intent: Intent?, startId: Int) {
        super.onStart(intent, startId)
        val notification = createNotification()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                /* id = */ NOTIFICATION_IDENTIFIER, // Cannot be 0
                /* notification = */ notification,
                /* foregroundServiceType = */ ServiceInfo.FOREGROUND_SERVICE_TYPE_CONNECTED_DEVICE
            )
        } else {
            startForeground(
                /* id = */ NOTIFICATION_IDENTIFIER, // Cannot be 0
                /* notification = */ notification,
            )
        }
        thread.start()
    }

    private fun createNotification(): Notification {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_MAX
            )
            notificationManager.createNotificationChannel(channel)
        }

        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.btn_radio)
            .setContentTitle("Health recovery assistant")
            .setContentText("Health recovery assistant is running")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setOngoing(true)
            .setShowWhen(false)
            .build()
    }

    private fun getForegroundServiceThread(): Thread = thread(start = false) {
        while (!isInterrupted) {
            try {
                val uuid = queue.take()
                synchronized(uuid) {
                    WorkManager.getInstance(applicationContext)
                        .getWorkInfoByIdFlow(uuid)
                        .onEach { workInfo ->
                            if (workInfo?.id == uuid && workInfo.state == WorkInfo.State.SUCCEEDED) {
                                Log.i("MYTAG", "9")
                                rescheduleWork(
                                    address = workInfo.outputData.getString(WorkManagerSource.KEY_DEVICE_ADDRESS) ?: "",
                                    workTag = workInfo.tags.last()
                                )
                            }
                        }.launchIn(CoroutineScope(Dispatchers.IO))
                }
            } catch (_: Exception) {
            }
        }
    }

    private suspend fun rescheduleWork(address: String, workTag: String) {
        Log.i("MYTAG", "10")
        when (val result = wearableLocalDataSource.getLatestWearableEvent()) {
            is BaseResult.Success -> {
                Log.i("MYTAG", "11")
                result.data?.let { wearableEventNotNull ->
                    WorkManager.getInstance(applicationContext).enqueue(
                        OneTimeWorkRequestBuilder<MiBand5Worker>()
                            .setInputData(
                                Data.Builder()
                                    .putString(WorkManagerSource.KEY_DEVICE_ADDRESS, address)
                                    .build()
                            )
                            .addTag(workTag).build()
                    )
                }
            }

            is BaseResult.Failure -> Unit
        }
    }

    companion object {
        private const val NOTIFICATION_IDENTIFIER = 1

        private const val NOTIFICATION_CHANNEL_ID = "ForegroundServiceChannel"

        private const val NOTIFICATION_CHANNEL_NAME = "ForegroundServiceChannel"
    }

    inner class LocalBinder : Binder() {
        fun getService(): ForegroundService = this@ForegroundService

        fun subscribeWork(id: UUID) {
            Log.i("MYTAG", "subscribe: $id")
            queue.add(id)
        }
    }
}