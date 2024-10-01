package by.bashlikovvv.common.remote.wearable

import com.google.android.gms.wearable.PutDataMapRequest

interface WearableRequestBuilderScope {
    fun putUByte(key: String, value: UByte)

    fun putLong(key: String, value: Long)

    fun putString(key: String, value: String)

    class Base(
        private val putDataMapRequest: PutDataMapRequest
    ) : WearableRequestBuilderScope {
        override fun putUByte(key: String, value: UByte) {
            putDataMapRequest.dataMap.putInt(key, value.toInt())
        }

        override fun putLong(key: String, value: Long) {
            putDataMapRequest.dataMap.putLong(key, value)
        }

        override fun putString(key: String, value: String) {
            putDataMapRequest.dataMap.putString(key, value)
        }
    }
}