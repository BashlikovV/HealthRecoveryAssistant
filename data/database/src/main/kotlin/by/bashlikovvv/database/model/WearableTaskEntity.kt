package by.bashlikovvv.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import by.bashlikovvv.database.contract.HRADRoomContract.WearableTasksTable

@Entity(tableName = WearableTasksTable.TABLE_NAME)
data class WearableTaskEntity(
    @[
        ColumnInfo(name = WearableTasksTable.COLUMN_ID)
        PrimaryKey(autoGenerate = true)
    ] val id: Long,
    @ColumnInfo(name = WearableTasksTable.COLUMN_VIBRATION_EVENTS)
    val vibrationEvents: String,
    @ColumnInfo(name = WearableTasksTable.COLUMN_NOTIFICATION_TEXT)
    val notificationText: String?,
)