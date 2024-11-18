package by.bashlikovvv.common.repository

import android.content.Context
import by.bashlikovvv.common.local.WearableEventsLocalDataSource
import by.bashlikovvv.domain.model.BluetoothDevice
import by.bashlikovvv.domain.model.WearableEvent
import by.bashlikovvv.domain.model.WearableEvents

class WearableRepository(
    private val wearableEventsLocalDataSource: WearableEventsLocalDataSource,
) {
    suspend fun scheduleHRAFileData(
        device: BluetoothDevice,
        context: Context,
        events: WearableEvents
    ) {
        wearableEventsLocalDataSource.clear()
        wearableEventsLocalDataSource.addWearableEvents(events)
        enqueueWearableWorkForDevice(
            device = device,
            context = context,
            event = events.events.minBy { it.scheduledTime }
        )
    }

    private fun enqueueWearableWorkForDevice(
        device: BluetoothDevice,
        context: Context,
        event: WearableEvent,
    ) {

    }
}