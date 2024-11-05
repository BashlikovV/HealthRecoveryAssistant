package by.bashlikovvv.database.views

import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import by.bashlikovvv.database.contract.HRADRoomContract.WearableEventsTable
import by.bashlikovvv.database.contract.HRADRoomContract.WearableTasksTable

@DatabaseView(
    value = """SELECT ${WearableEventsTable.COLUMN_ID}, ${WearableEventsTable.COLUMN_SCHEDULED_TIME}, ${WearableTasksTable.COLUMN_VIBRATION_EVENTS}, ${WearableTasksTable.COLUMN_NOTIFICATION_TEXT} 
           FROM ${WearableEventsTable.TABLE_NAME}
           JOIN ${WearableTasksTable.TABLE_NAME}
           ON ${WearableEventsTable.COLUMN_TASK_DESCRIPTION_KEY} = ${WearableTasksTable.COLUMN_ID}
           ORDER BY ${WearableEventsTable.COLUMN_SCHEDULED_TIME} 
           LIMIT 1;"""
)
data class WearableEventView(
    @ColumnInfo(name = WearableEventsTable.COLUMN_ID)
    val id: Long,
    @ColumnInfo(name = WearableEventsTable.COLUMN_SCHEDULED_TIME)
    val scheduledTime: Long,
    @ColumnInfo(name = WearableTasksTable.COLUMN_VIBRATION_EVENTS)
    val vibrationEventsString: String,
    @ColumnInfo(name = WearableTasksTable.COLUMN_NOTIFICATION_TEXT)
    val notificationText: String,
)