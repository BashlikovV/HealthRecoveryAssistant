package by.bashlikovvv.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import by.bashlikovvv.database.model.WearableTaskEntity

@Dao
interface WearableTasksDao {
    @Insert(
        entity = WearableTaskEntity::class,
        onConflict = OnConflictStrategy.Companion.REPLACE
    )
    suspend fun addWearableTask(wearableTaskEntity: WearableTaskEntity): Long
}