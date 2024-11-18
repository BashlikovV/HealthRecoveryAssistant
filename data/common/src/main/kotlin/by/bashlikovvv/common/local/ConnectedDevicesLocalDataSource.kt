package by.bashlikovvv.common.local

import by.bashlikovvv.database.dao.ConnectedDevicesDao
import by.bashlikovvv.database.mapper.ConnectedDeviceEntityToBluetoothDeviceMapper
import by.bashlikovvv.domain.base.AppDispatchers
import by.bashlikovvv.domain.base.BaseResult
import by.bashlikovvv.domain.model.BluetoothDevice
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.IOException

class ConnectedDevicesLocalDataSource(
    appDispatchers: AppDispatchers,
    private val connectedDevicesDao: ConnectedDevicesDao,
) {
    private val ioDispatcher = appDispatchers.io

    private val mapper = ConnectedDeviceEntityToBluetoothDeviceMapper()

    suspend fun addConnectedDevice(device: BluetoothDevice): BaseResult<Long> = withContext(ioDispatcher) {
        try {
            BaseResult.Success(connectedDevicesDao.addDevice(mapper.mapToEntity(device).copy(id = 0)))
        } catch (e: IOException) {
            BaseResult.Failure(e)
        }
    }

    fun getConnectedDevices(): Flow<List<BluetoothDevice>> {
        return try {
            connectedDevicesDao.getDevices()
                .map { list -> list.map { mapper.mapFromEntity(it) } }
        } catch (_: IOException) {
            flowOf()
        }
    }

    suspend fun getDeviceById(id: Long): BaseResult<BluetoothDevice?> = withContext(ioDispatcher) {
        try {
            BaseResult.Success(
                connectedDevicesDao.getDevice(id)
                    ?.let { mapper.mapFromEntity(it) }
            )
        } catch (e: IOException) {
            BaseResult.Failure(e)
        }
    }

    suspend fun removeConnectedDevice(id: Long): BaseResult<Boolean> = withContext(ioDispatcher) {
        try {
            BaseResult.Success(connectedDevicesDao.removeDevice(id) > 0)
        } catch (e: IOException) {
            BaseResult.Failure(e)
        }
    }

    suspend fun updateConnectedDevice(device: BluetoothDevice): BaseResult<Boolean> = withContext(ioDispatcher) {
        try {
            BaseResult.Success(
                connectedDevicesDao.updateDevice(mapper.mapToEntity(device)) > 0
            )
        } catch (e: IOException) {
            BaseResult.Failure(e)
        }
    }
}