package by.bashlikovvv.common.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import by.bashlikovvv.common.local.model.WearableTaskEntity

@Dao
interface WearableTasksDao {
    @Insert(
        entity = WearableTaskEntity::class,
        onConflict = OnConflictStrategy.Companion.REPLACE
    )
    suspend fun addWearableTask(wearableTaskEntity: WearableTaskEntity): Long
}