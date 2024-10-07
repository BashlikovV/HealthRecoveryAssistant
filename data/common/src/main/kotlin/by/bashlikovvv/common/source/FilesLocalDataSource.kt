package by.bashlikovvv.common.source

import android.content.ContentResolver
import android.net.Uri
import by.bashlikovvv.common.model.WearableEventsDbo
import by.bashlikovvv.domain.base.AppDispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FilesLocalDataSource(
    appDispatchers: AppDispatchers,
    private val contentResolver: ContentResolver,
) {
    private val ioDispatcher = appDispatchers.io

    suspend fun openFile(uri: Uri): WearableEventsDbo? = withContext(ioDispatcher) {
        suspendCoroutine<WearableEventsDbo?> { continuation ->
            val jsonString = contentResolver
                .openInputStream(uri)
                ?.buffered()
                ?.let { stream ->
                    val reader = BufferedReader(InputStreamReader(stream))
                    val stringBuilder = StringBuilder()
                    var line: String? = null

                    while (reader.readLine()?.let { line = it } != null) {
                        stringBuilder.append("$line\n")
                    }
                    reader.close()

                    stringBuilder.toString()
                }

            continuation.resume(jsonString?.let { Json.decodeFromString(it) })
        }
    }
}