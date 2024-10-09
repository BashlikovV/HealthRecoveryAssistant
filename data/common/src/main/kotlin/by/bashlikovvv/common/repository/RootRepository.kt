package by.bashlikovvv.common.repository

import android.content.Context
import android.net.Uri
import by.bashlikovvv.common.mapper.WearableEventsDboToWearableEventsMapper
import by.bashlikovvv.common.remote.wearable.WearableRemoteDataSource
import by.bashlikovvv.common.source.FilesLocalDataSource
import by.bashlikovvv.common.source.decodeFromJsonString
import by.bashlikovvv.domain.base.BaseResult
import by.bashlikovvv.domain.model.WearableEvents

class RootRepository(
    private val wearableRemoteDataSource: WearableRemoteDataSource,
    private val filesLocalDataSource: FilesLocalDataSource,
) {
    fun initialize(context: Context) {
        wearableRemoteDataSource.initialize(context)
    }

    fun destroy() {
        wearableRemoteDataSource.destroy()
    }

    suspend fun openHRAFile(uri: Uri): BaseResult<WearableEvents?> {
        val mapper = WearableEventsDboToWearableEventsMapper()

        return try {
            BaseResult.Success(
                filesLocalDataSource.openFile(uri)?.let {
                    mapper.mapFromEntity(it.decodeFromJsonString())
                }
            )
        } catch (e: Exception) {
            BaseResult.Failure(e)
        }
    }
}