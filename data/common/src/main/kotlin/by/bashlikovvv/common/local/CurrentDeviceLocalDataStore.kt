package by.bashlikovvv.common.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import by.bashlikovvv.domain.base.AppDispatchers
import by.bashlikovvv.domain.model.BluetoothDevice
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

private const val USER_PREFERENCES = "by.bashlikovvv.data.common.homescreenlocaldatastore"

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = USER_PREFERENCES)

class CurrentDeviceLocalDataStore(
    context: Context,
    appDispatchers: AppDispatchers,
    private val devicesLocalDataSource: ConnectedDevicesLocalDataSource,
) {
    private val dataStore = context.dataStore

    private val ioDispatcher = appDispatchers.io

    suspend fun setCurrentDevice(device: BluetoothDevice) {
        withContext(ioDispatcher) {
            dataStore.edit { preferences ->
                preferences[KEY_ID] = device.id
            }
        }
    }

    suspend fun getCurrentDevice() = withContext(ioDispatcher) {
        dataStore.data.first().let { preferences ->
            preferences[KEY_ID]?.let { devicesLocalDataSource.getDeviceById(it) }
        }
    }

    companion object {
        private val KEY_ID = longPreferencesKey("id")
    }
}