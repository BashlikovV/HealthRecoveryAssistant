package by.bashlikovvv.home.presentation.ui.store

import android.app.Activity.RESULT_OK
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.result.ActivityResult
import by.bashlikovvv.common.repository.BluetoothRepository
import by.bashlikovvv.common.repository.HARFilesRepository
import by.bashlikovvv.common.repository.WearableRepository
import by.bashlikovvv.domain.base.BaseResult
import by.bashlikovvv.domain.model.ReminderDescription
import by.bashlikovvv.domain.model.WearableEvents
import by.bashlikovvv.home.domain.model.DevicesListItems
import by.bashlikovvv.home.presentation.ui.store.HomeStore.*
import by.bashlikovvv.home.presentation.ui.store.HomeStoreFactory.*
import by.bashlikovvv.ui.base.BaseCoroutineExecutor
import org.koin.core.component.inject
import java.util.Calendar
import java.util.TimeZone

internal class HomeStoreExecutor : BaseCoroutineExecutor<Intent, Action, State, Msg, Nothing>() {
    private val harFilesRepository: HARFilesRepository by inject()

    private val wearableRepository: WearableRepository by inject()

    private val bluetoothRepository: BluetoothRepository by inject()

    override fun executeIntent(intent: Intent, getState: () -> State) {
        when (intent) {
            is Intent.OnActivityResult -> onActivityResultIntent(intent.activityResult)
            is Intent.ScheduleFileData -> onScheduleFileDataIntent(intent.events, intent.context)
            is Intent.DeviceClick -> onDeviceClick(intent.device)
            is Intent.Vibrate -> vibrate()
            is Intent.SetVibrationProfile -> launchIO {
                bluetoothRepository.setVibrationProfile(
                    test = false,
                    repeat = intent.data.second,
                    onOffSequence = intent.data.first
                )
            }
        }
    }

    override fun executeAction(action: Action, getState: () -> State) {
        when(action) {
            is Action.Initialize -> initialize()
            is Action.InitializeWithHARFile -> openHARFile(Uri.parse(action.uri))
        }
    }

    private fun onDeviceClick(device: DevicesListItems.Device) {
        launchIO {
           if (bluetoothRepository.connect(device.address)) {
               dispatchOnMainThread(Msg.DeviceConnected(device))
           }
        }
    }

    private fun initialize() {
        launchIO {
            bluetoothRepository.connectedDevices.collect { devices ->
                dispatchOnMainThread(
                    Msg.Devices(
                        devices = devices.map { localDevice ->
                            DevicesListItems.Device(
                                id = localDevice.id.toInt(),
                                name = localDevice.name,
                                address = localDevice.address,
                                connected = bluetoothRepository.isConnected(localDevice.address)
                            )
                        }
                    )
                )
            }
        }
    }

    private fun onActivityResultIntent(result: ActivityResult) {
        if (result.resultCode == RESULT_OK) {
            result.data?.data?.let { openHARFile(it) }
        }
    }

    private fun openHARFile(uri: Uri) = launchIO(
        safeAction = {
            when(val rResult = harFilesRepository.openHRAFile(uri)) {
                is BaseResult.Success -> dispatchOnMainThread(
                    Msg.HRAFileData(
                        name = uri.lastPathSegment ?: "null",
                        data = rResult.data
                    )
                )
                is BaseResult.Failure -> Unit
            }
        }
    )

    private fun onScheduleFileDataIntent(
        events: WearableEvents,
        context: Context,
    ) {
        launchIO {
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            calendar.add(Calendar.MINUTE, 1)
            bluetoothRepository.sendCreateReminderCommand(
                ReminderDescription(
                    message = "test msg",
                    date = calendar.time
                )
            )
        }
//        launchIO(
//            safeAction = { wearableRepository.scheduleHRAFileData(context, events) },
//        )
    }

    private fun vibrate() {
        launchIO {
            bluetoothRepository.sendFindDeviceCommand(true)
        }
    }
}