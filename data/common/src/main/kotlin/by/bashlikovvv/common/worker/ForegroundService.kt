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
import by.bashlikovvv.common.local.CurrentDeviceLocalDataStore
import by.bashlikovvv.common.local.WearableEventsLocalDataSource
import by.bashlikovvv.common.repository.BluetoothRepository
import by.bashlikovvv.domain.base.BaseResult
import by.bashlikovvv.domain.model.BluetoothDevice
import by.bashlikovvv.domain.model.NotificationTypes
import by.bashlikovvv.domain.model.ReminderDescription
import by.bashlikovvv.domain.model.WearableEvent
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.runBlocking
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.Calendar
import java.util.Date
import java.util.TimeZone
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

class ForegroundService : Service(), KoinComponent {

    private val notificationManager by lazy {
        getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    }

    private val scheduler = Executors.newSingleThreadScheduledExecutor()

    private var countDownLatch: CountDownLatch? = null

    private val wearableEventsLocalDataSource: WearableEventsLocalDataSource by inject()

    private val bluetoothRepository: BluetoothRepository by inject()

    private val currentDeviceLocalDataStore: CurrentDeviceLocalDataStore by inject()

    private var isInterrupted: Boolean = false

    private val thread = getForegroundServiceThread()

    private val binder = LocalBinder()

    override fun onBind(p0: Intent?): IBinder = binder

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!isRunning) {
            isRunning = true
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
        return START_STICKY
    }

    override fun onDestroy() {
        isRunning = false
        super.onDestroy()
    }

    private fun createNotification(): Notification {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
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

    private suspend fun getLatestEvent(): WearableEvent? {
        return when(val result = wearableEventsLocalDataSource.getLatestWearableEvent()) {
            is BaseResult.Success -> result.data
            is BaseResult.Failure -> null
        }
    }

    private fun getForegroundServiceThread(): Thread = thread(start = false) {
        runBlocking(Executors.newSingleThreadExecutor().asCoroutineDispatcher()) {
            loop@while (!isInterrupted) {
                try {
                    when(val latestEvent = wearableEventsLocalDataSource.getLatestWearableEvent()) {
                        is BaseResult.Success -> {
                            if (latestEvent.data == null) continue@loop
                            val delay = (latestEvent.data?.scheduledTime ?: 0) - System.currentTimeMillis()
                            if (delay <= 0) {
                                wearableEventsLocalDataSource.removeLatestEvent()
                            } else {
                                latestEvent.data?.let { event ->
                                    repeat(2) {
                                        dispatchEvent(event)
                                    }
                                }
                                wearableEventsLocalDataSource.removeLatestEvent()
                                getLatestEvent()?.let { event ->
                                    val delay = event.scheduledTime - System.currentTimeMillis() - 90_000
                                    if (delay > 0) {
                                        countDownLatch = CountDownLatch(1)
                                        scheduler.schedule(
                                            /* command */ { countDownLatch?.countDown() },
                                            /* delay */ event.scheduledTime - System.currentTimeMillis() - 90_000,
                                            /* unit */ TimeUnit.MILLISECONDS
                                        )
                                        countDownLatch?.await()
                                        countDownLatch = null
                                    }
                                }
                            }
                        }
                        is BaseResult.Failure -> Unit
                    }
                } catch (_: Exception) {
                }
            }
        }
    }

    private suspend fun dispatchEvent(wearableEvent: WearableEvent): Long {
        currentDeviceLocalDataStore.getDevice()?.address?.let { address ->
            var count = 0
            while (!bluetoothRepository.connect(address) && count < 5) {
                count++
            }
        }
        with(wearableEvent.vibrationDescriptor) {
            bluetoothRepository.setVibrationProfile(
                notificationType = NotificationTypes.EventReminder(),
                test = false,
                repeat = repeat,
                onOffSequence = onOffSequence
            )
        }
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        calendar.timeInMillis = wearableEvent.scheduledTime
        Log.i("MYTAG", "current time: ${Date(System.currentTimeMillis())}, scheduled time: ${calendar.time}, action: ${wearableEvent.notificationText}")
        bluetoothRepository.sendCreateReminderCommand(
            ReminderDescription(
                message = wearableEvent.notificationText,
                date = calendar.time,
            )
        )
        return wearableEvent.scheduledTime
    }

    private suspend fun CurrentDeviceLocalDataStore.getDevice(): BluetoothDevice? =
        when(val result = this.getCurrentDevice()) {
            is BaseResult.Success -> result.data
            else -> null
        }

    companion object {
        private const val NOTIFICATION_IDENTIFIER = 1

        private const val NOTIFICATION_CHANNEL_ID = "ForegroundServiceChannel"

        private const val NOTIFICATION_CHANNEL_NAME = "ForegroundServiceChannel"

        private var isRunning: Boolean = false
    }

    inner class LocalBinder : Binder()
}