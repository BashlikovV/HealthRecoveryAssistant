package by.bashlikovvv.common.remote.wearable

import android.content.Context
import by.bashlikovvv.common.worker.WearableEventsWorker.Companion.Notification
import by.bashlikovvv.common.worker.WearableEventsWorker.Companion.Vibrate
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.concurrent.ThreadLocalRandom
import kotlin.coroutines.resume

class WearableRemoteDataSource {
    private var dataClient: DataClient? = null

    private val random = ThreadLocalRandom.current()

    fun initialize(context: Context) {
        dataClient = Wearable.getDataClient(context)
    }

    fun destroy() {
        dataClient = null
    }

    suspend fun vibrate(
        duration: Long,
        amplitude: UByte,
    ) {
        putData(
            path = Vibrate.PATH,
            requestBuilder = {
                putLong(Vibrate.Keys.DURATION, duration)
                putUByte(Vibrate.Keys.AMPLITUDE, amplitude)
            }
        )
    }

    suspend fun showNotification(text: String) {
        putData(
            path = Notification.PATH,
            requestBuilder = {
                putString(Notification.Keys.TEXT, text)
            }
        )
    }

    suspend fun putData(
        path: String,
        requestBuilder: WearableRequestBuilderScope.() -> Unit,
    ) = suspendCancellableCoroutine { continuation ->
        dataClient?.putDataItem(
            PutDataMapRequest.create(path).run {
                WearableRequestBuilderScope.Base(this).requestBuilder()
                dataMap.putInt("random key", random.nextInt())
                asPutDataRequest()
            }
        )?.also { task ->
            with(task) {
                addOnFailureListener { continuation.resume(false) }
                addOnSuccessListener { continuation.resume(true) }
            }
        }
    }
}