package by.bashlikovvv.common.repository

import android.net.Uri
import by.bashlikovvv.common.local.FilesLocalDataSource
import by.bashlikovvv.common.local.decodeFromJsonString
import by.bashlikovvv.common.mapper.WearableEventsDboToWearableEventsMapper
import by.bashlikovvv.domain.base.BaseResult
import by.bashlikovvv.domain.model.WearableEvents

class HARFilesRepository(
    private val filesLocalDataSource: FilesLocalDataSource,
) {
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