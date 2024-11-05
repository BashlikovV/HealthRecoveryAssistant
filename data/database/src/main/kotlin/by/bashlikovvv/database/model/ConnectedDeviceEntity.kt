package by.bashlikovvv.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import by.bashlikovvv.database.contract.HRADRoomContract.ConnectedDevices

@Entity(
    tableName = ConnectedDevices.TABLE_NAME,
    indices = [
        Index(
            value = [ConnectedDevices.COLUMN_DEVICE_ADDRESS],
            unique = true,
        )
    ]
)
data class ConnectedDeviceEntity(
    @[
        ColumnInfo(name = ConnectedDevices.COLUMN_ID)
        PrimaryKey(autoGenerate = true)
    ]
    val id: Long,
    @ColumnInfo(name = ConnectedDevices.COLUMN_DEVICE_NAME)
    val name: String,
    @ColumnInfo(name = ConnectedDevices.COLUMN_DEVICE_ADDRESS)
    val address: String,
    @ColumnInfo(name = ConnectedDevices.COLUMN_DEVICE_TYPE)
    val type: String,
)