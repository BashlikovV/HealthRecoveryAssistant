package by.bashlikovvv.common.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import by.bashlikovvv.common.local.WearableEventsLocalDataSource
import by.bashlikovvv.common.repository.BluetoothRepository
import by.bashlikovvv.domain.base.BaseResult
import by.bashlikovvv.domain.model.NotificationTypes
import by.bashlikovvv.domain.model.ReminderDescription
import by.bashlikovvv.domain.model.WearableEvent
import kotlinx.coroutines.delay
import java.util.Calendar
import java.util.TimeZone

class MiBand5Worker(
    params: WorkerParameters,
    private val appContext: Context,
    private val wearableLocalDataSource: WearableEventsLocalDataSource,
    private val bluetoothRepository: BluetoothRepository,
) : CoroutineWorker(appContext, params) {
    private val address = params.inputData.getString(WorkManagerSource.KEY_DEVICE_ADDRESS) ?: ""

    override suspend fun doWork(): Result {
        if (connect(address)) {
            ForegroundServiceContract().trackWorker(applicationContext, id)
            when (val result = wearableLocalDataSource.getLatestWearableEvent()) {
                is BaseResult.Success -> {
                    result.data?.let { dispatchEvent(it) }
                    wearableLocalDataSource.removeLatestEvent()
                }

                is BaseResult.Failure -> Unit
            }
        }
        return Result.success(
            Data.Builder()
                .putString(WorkManagerSource.KEY_DEVICE_ADDRESS, address)
                .build()
        )
    }

    private suspend fun connect(address: String): Boolean {
        repeat(5) {
            if (bluetoothRepository.connect(address)) return true
        }

        return false
    }

    private suspend fun dispatchEvent(wearableEvent: WearableEvent) {
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
        bluetoothRepository.sendCreateReminderCommand(
            ReminderDescription(
                message = wearableEvent.notificationText,
                date = calendar.time,
            )
        )
        delay(wearableEvent.scheduledTime - System.currentTimeMillis())
    }

    interface Factory {
        fun create(appContext: Context, params: WorkerParameters): MiBand5Worker

        class Base(
            private val wearableLocalDataSource: WearableEventsLocalDataSource,
            private val bluetoothRepository: BluetoothRepository,
        ) : Factory {
            override fun create(
                appContext: Context,
                params: WorkerParameters,
            ): MiBand5Worker = MiBand5Worker(
                appContext = appContext,
                params = params,
                wearableLocalDataSource = wearableLocalDataSource,
                bluetoothRepository = bluetoothRepository,
            )
        }
    }
}