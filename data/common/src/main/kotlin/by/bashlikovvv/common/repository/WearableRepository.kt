package by.bashlikovvv.common.repository

import android.content.Context
import by.bashlikovvv.common.remote.wearable.WearableRemoteDataSource

class WearableRepository(
    private val wearableRemoteDataSource: WearableRemoteDataSource,
) {
    fun initialize(context: Context) = wearableRemoteDataSource.initialize(context)

    fun destroy() = wearableRemoteDataSource.destroy()
}