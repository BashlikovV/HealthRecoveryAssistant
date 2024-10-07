package by.bashlikovvv.common.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import by.bashlikovvv.common.local.contract.HRADRoomContract.WearableTasksTable
import by.bashlikovvv.common.local.contract.HRADRoomContract.WearableEventsTable

@Entity(
    tableName = WearableEventsTable.TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            entity = WearableTaskEntity::class,
            parentColumns = [WearableTasksTable.COLUMN_ID],
            childColumns = [WearableEventsTable.COLUMN_TASK_DESCRIPTION_KEY],
            onDelete = ForeignKey.Companion.CASCADE,
            onUpdate = ForeignKey.Companion.CASCADE,
            deferred = true,
        )
    ]
)
data class WearableEventEntity(
    @[
        ColumnInfo(name = WearableEventsTable.COLUMN_ID)
        PrimaryKey(autoGenerate = true)
    ] val id: Long,
    @ColumnInfo(name = WearableEventsTable.COLUMN_SCHEDULED_TIME)
    val scheduledTime: Long,
    @ColumnInfo(
        name = WearableEventsTable.COLUMN_TASK_DESCRIPTION_KEY,
        index = true
    ) val taskDescription: Long,
)