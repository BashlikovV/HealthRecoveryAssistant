package by.bashlikovvv.common.source

import by.bashlikovvv.common.mapper.WearableEventToWearableEventEntityMapper
import by.bashlikovvv.common.mapper.WearableEventToWearableTaskEntityMapper
import by.bashlikovvv.common.mapper.WearableEventViewToWearableEventMapper
import by.bashlikovvv.database.dao.WearableEventsDao
import by.bashlikovvv.database.dao.WearableTasksDao
import by.bashlikovvv.domain.base.AppDispatchers
import by.bashlikovvv.domain.base.BaseResult
import by.bashlikovvv.domain.model.WearableEvent
import by.bashlikovvv.domain.model.WearableEvents
import kotlinx.coroutines.withContext
import java.io.IOException

class WearableEventsLocalDataSource(
    appDispatchers: AppDispatchers,
    private val wearableEventsDao: WearableEventsDao,
    private val wearableTasksDao: WearableTasksDao,
) {
    private val ioDispatcher = appDispatchers.io

    suspend fun addWearableEvent(wearableEvent: WearableEvent): BaseResult<Long> =
        withContext(ioDispatcher) {
            try {
                val newTaskId = wearableTasksDao.addWearableTask(
                    WearableEventToWearableTaskEntityMapper()
                        .mapFromEntity(wearableEvent)
                )
                BaseResult.Success(
                    wearableEventsDao.addWearableEvent(
                        WearableEventToWearableEventEntityMapper(newTaskId).mapFromEntity(
                            wearableEvent
                        )
                    )
                )
            } catch (e: IOException) {
                BaseResult.Failure(e)
            }
        }

    suspend fun addWearableEvents(events: WearableEvents): BaseResult<List<Long>> =
        withContext(ioDispatcher) {
            val toTaskMapper = WearableEventToWearableTaskEntityMapper()
            val createdIndices = mutableListOf<Long>()

            try {
                wearableEventsDao.clearEvents()
                events.events.forEach { event ->
                    val newTaskId = wearableTasksDao.addWearableTask(
                        toTaskMapper.mapFromEntity(event)
                    )
                    val newEventId = wearableEventsDao.addWearableEvent(
                        WearableEventToWearableEventEntityMapper(newTaskId)
                            .mapFromEntity(event)
                    )
                    createdIndices.add(newEventId)
                }
                BaseResult.Success(createdIndices)
            } catch (e: IOException) {
                BaseResult.Failure(e)
            }
        }

    suspend fun getLatestWearableEvent(): BaseResult<WearableEvent?> = withContext(ioDispatcher) {
        val mapper = WearableEventViewToWearableEventMapper()

        try {
            BaseResult.Success(
                wearableEventsDao.getWearableEventView()
                    ?.let { mapper.mapFromEntity(it) }
            )
        } catch (e: IOException) {
            BaseResult.Failure(e)
        }
    }

    suspend fun removeLatestEvent(): BaseResult<Unit> = withContext(ioDispatcher) {
        try {
            if (wearableEventsDao.removeLatestWearableEvent() == DatabaseResult.SUCCESS) {
                BaseResult.Success(Unit)
            } else {
                BaseResult.Failure(NullPointerException())
            }
        } catch (e: IOException) {
            BaseResult.Failure(e)
        }
    }

    suspend fun removeEventById(id: Long): BaseResult<Unit> = withContext(ioDispatcher) {
        try {
            val result = wearableEventsDao.removeWearableEventById(id)
            if (result == DatabaseResult.SUCCESS) {
                BaseResult.Success(Unit)
            } else {
                BaseResult.Failure(NullPointerException())
            }
        } catch (e: IOException) {
            BaseResult.Failure(e)
        }
    }

    suspend fun clear() = withContext(ioDispatcher) {
        wearableEventsDao.clearEvents()
    }

    companion object {
        object DatabaseResult {
            const val SUCCESS = 1
            const val FAILURE = 0
        }
    }
}