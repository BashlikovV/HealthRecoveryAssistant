package by.bashlikovvv.database

import androidx.room.Database
import androidx.room.RoomDatabase
import by.bashlikovvv.database.dao.WearableEventsDao
import by.bashlikovvv.database.dao.WearableTasksDao
import by.bashlikovvv.database.model.WearableEventEntity
import by.bashlikovvv.database.model.WearableTaskEntity
import by.bashlikovvv.database.views.WearableEventView

@Database(
    version = 1,
    entities = [WearableEventEntity::class, WearableTaskEntity::class],
    views = [WearableEventView::class]
)
abstract class HRADatabase : RoomDatabase() {
    abstract val wearableTasksDao: WearableTasksDao

    abstract val wearableEventsDao: WearableEventsDao
}