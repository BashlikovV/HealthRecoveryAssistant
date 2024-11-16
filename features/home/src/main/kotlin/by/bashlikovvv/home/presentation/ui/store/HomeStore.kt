package by.bashlikovvv.home.presentation.ui.store

import android.content.Context
import android.os.Parcelable
import androidx.activity.result.ActivityResult
import by.bashlikovvv.domain.model.BluetoothDevice
import by.bashlikovvv.domain.model.WearableEvents
import by.bashlikovvv.home.domain.model.DevicesListItems
import by.bashlikovvv.home.presentation.ui.store.HomeStore.*
import com.arkivanov.mvikotlin.core.store.Store
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.parcelize.Parcelize

interface HomeStore : Store<Intent, State, Nothing> {
    sealed class Intent {
        data class OnActivityResult(val activityResult: ActivityResult) : Intent()

        data class ScheduleFileData(
            val result: ActivityResult,
            val device: DevicesListItems.Device,
            val context: Context,
        ) : Intent()

        data class DeviceClick(
            val device: DevicesListItems.Device
        ) : Intent()

        data object Vibrate : Intent()
    }

    @Parcelize
    data class State(
        val fileName: String? = null,
        val fileContent: WearableEvents? = null,
        val devicesList: ImmutableList<DevicesListItems> = persistentListOf(),
        val isInSelectionMode: Boolean = false,
        val connectedDevice: BluetoothDevice? = null,
    ) : Parcelable
}