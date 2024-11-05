package by.bashlikovvv.healthrecoveryassistant.di

import androidx.room.Room
import by.bashlikovvv.database.HRADatabase
import by.bashlikovvv.database.contract.HRADRoomContract
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(
            context = androidContext(),
            klass = HRADatabase::class.java,
            name = HRADRoomContract.DATABASE_NAME
        ).build()
    }

    single {
        val database: HRADatabase = get()
        database.wearableTasksDao
    }

    single {
        val database: HRADatabase = get()
        database.wearableEventsDao
    }

    single {
        val database: HRADatabase = get()
        database.connectedDevicesDao
    }
}