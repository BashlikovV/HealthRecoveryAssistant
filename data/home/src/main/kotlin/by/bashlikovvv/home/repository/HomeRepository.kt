package by.bashlikovvv.home.repository

import android.content.Context
import android.net.Uri
import by.bashlikovvv.common.mapper.WearableEventsDboToWearableEventsMapper
import by.bashlikovvv.common.source.FilesLocalDataSource
import by.bashlikovvv.common.source.WearableEventsLocalDataSource
import by.bashlikovvv.common.source.WorkManagerSource
import by.bashlikovvv.domain.base.BaseResult
import by.bashlikovvv.domain.model.WearableEvent
import by.bashlikovvv.domain.model.WearableEvents

class HomeRepository(
    private val wearableEventsLocalDataSource: WearableEventsLocalDataSource,
    private val filesLocalDataSource: FilesLocalDataSource,
    private val workManagerSource: WorkManagerSource,
) {
    suspend fun openHRAFile(uri: Uri): BaseResult<WearableEvents?> {
        val mapper = WearableEventsDboToWearableEventsMapper()

        return try {
            BaseResult.Success(
                filesLocalDataSource.openFile(uri)?.let { mapper.mapFromEntity(it) }
            )
        } catch (e: Exception) {
            BaseResult.Failure(e)
        }
    }

    suspend fun scheduleHRAFileData(
        context: Context,
        events: WearableEvents
    ) {
        wearableEventsLocalDataSource.clear()
        wearableEventsLocalDataSource.addWearableEvents(events)
        enqueueWearableWork(
            context = context,
            event = events.events.minBy { it.scheduledTime }
        )
    }

    private fun enqueueWearableWork(
        context: Context,
        event: WearableEvent,
    ) {
        workManagerSource.enqueueUniqueWork(
            context,
            event.scheduledTime - System.currentTimeMillis(),
        )
    }
}