package by.bashlikovvv.common.repository

import android.content.Context
import by.bashlikovvv.common.local.WearableEventsLocalDataSource
import by.bashlikovvv.common.worker.WorkManagerSource
import by.bashlikovvv.domain.model.WearableEvent
import by.bashlikovvv.domain.model.WearableEvents

class WearableRepository(
    private val wearableEventsLocalDataSource: WearableEventsLocalDataSource,
    private val workManagerSource: WorkManagerSource,
) {
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