package by.bashlikovvv.common.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import by.bashlikovvv.common.local.contract.HRADRoomContract.WearableEventsTable
import by.bashlikovvv.common.local.model.WearableEventEntity
import by.bashlikovvv.common.local.views.WearableEventView

@Dao
interface WearableEventsDao {
    @Insert(
        entity = WearableEventEntity::class,
        onConflict = OnConflictStrategy.Companion.REPLACE
    )
    suspend fun addWearableEvent(entity: WearableEventEntity): Long

    @[
        Transaction
        Query(
            """DELETE 
               FROM ${WearableEventsTable.TABLE_NAME} 
               WHERE ${WearableEventsTable.COLUMN_ID}=:id;"""
        )
    ]
    suspend fun removeWearableEventById(id: Long): Int

    @Query("SELECT * FROM WearableEventView")
    suspend fun getWearableEventView(): WearableEventView?

    @[
        Transaction
        Query(
            """DELETE 
               FROM ${WearableEventsTable.TABLE_NAME};"""
        )
    ]
    suspend fun clearEvents()
}