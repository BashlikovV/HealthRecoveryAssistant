package by.bashlikovvv.common.local

import androidx.room.Database
import androidx.room.RoomDatabase
import by.bashlikovvv.common.local.dao.WearableEventsDao
import by.bashlikovvv.common.local.dao.WearableTasksDao
import by.bashlikovvv.common.local.model.WearableEventEntity
import by.bashlikovvv.common.local.model.WearableTaskEntity
import by.bashlikovvv.common.local.views.WearableEventView

@Database(
    version = 1,
    entities = [WearableEventEntity::class, WearableTaskEntity::class],
    views = [WearableEventView::class]
)
abstract class HRADatabase : RoomDatabase() {
    abstract val wearableTasksDao: WearableTasksDao

    abstract val wearableEventsDao: WearableEventsDao
}