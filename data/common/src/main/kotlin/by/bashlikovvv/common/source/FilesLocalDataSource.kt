package by.bashlikovvv.common.source

import android.content.ContentResolver
import android.net.Uri
import by.bashlikovvv.domain.base.AppDispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
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

    suspend fun openFile(uri: Uri): String? = withContext(ioDispatcher) {
        suspendCoroutine<String?> { continuation ->
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

            continuation.resume(jsonString)
        }
    }
}

inline fun <reified T : Any>String.decodeFromJsonString(): T = Json.decodeFromString<T>(this)

inline fun <reified T : Any> T.encodeToJsonString(): String = Json.encodeToString(this)