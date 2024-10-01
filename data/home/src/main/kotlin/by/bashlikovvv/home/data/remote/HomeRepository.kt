package by.bashlikovvv.home.data.remote

import android.content.Context
import by.bashlikovvv.common.remote.wearable.WearableRemoteDataSource
import by.bashlikovvv.domain.base.AppDispatchers
import by.bashlikovvv.domain.model.WearableEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HomeRepository(
    appDispatchers: AppDispatchers,
    private val wearableRemoteDataSource: WearableRemoteDataSource,
) {
    private val ioDispatcher = appDispatchers.io

    fun initialize(context: Context) {
        wearableRemoteDataSource.initialize(context)
    }

    fun destroy() = wearableRemoteDataSource.destroy()

    fun dispatchEvent(wearableEvent: WearableEvent) {
        CoroutineScope(ioDispatcher).launch {
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
}