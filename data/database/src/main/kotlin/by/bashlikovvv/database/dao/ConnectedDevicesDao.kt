package by.bashlikovvv.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import by.bashlikovvv.database.contract.HRADRoomContract.ConnectedDevices
import by.bashlikovvv.database.model.ConnectedDeviceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ConnectedDevicesDao {
    @Insert(
        entity = ConnectedDeviceEntity::class,
        onConflict = OnConflictStrategy.REPLACE,
    )
    suspend fun addDevice(deviceEntity: ConnectedDeviceEntity): Long

    @Query("""SELECT * 
              FROM ${ConnectedDevices.TABLE_NAME};""")
    fun getDevices(): Flow<List<ConnectedDeviceEntity>>

    @Query("""SELECT * 
              FROM ${ConnectedDevices.TABLE_NAME} 
              WHERE ${ConnectedDevices.COLUMN_ID} = :id;""")
    suspend fun getDevice(id: Long): ConnectedDeviceEntity?

    @[
        Transaction
        Query("""DELETE 
                 FROM ${ConnectedDevices.TABLE_NAME} 
                 WHERE ${ConnectedDevices.COLUMN_ID} = :id;""")
    ]
    suspend fun removeDevice(id: Long): Int

    @[
        Transaction
        Update(
            entity = ConnectedDeviceEntity::class,
            onConflict = OnConflictStrategy.REPLACE,
        )
    ]
    suspend fun updateDevice(deviceEntity: ConnectedDeviceEntity): Int
}