package by.bashlikovvv.home.presentation.ui.store

import android.app.Activity.RESULT_OK
import android.content.Context
import android.net.Uri
import androidx.activity.result.ActivityResult
import by.bashlikovvv.common.repository.BluetoothRepository
import by.bashlikovvv.common.repository.HARFilesRepository
import by.bashlikovvv.common.repository.WearableRepository
import by.bashlikovvv.domain.base.BaseResult
import by.bashlikovvv.domain.model.WearableEvents
import by.bashlikovvv.home.domain.model.DevicesListItems
import by.bashlikovvv.home.presentation.ui.store.HomeStore.*
import by.bashlikovvv.home.presentation.ui.store.HomeStoreFactory.*
import by.bashlikovvv.ui.base.BaseCoroutineExecutor
import org.koin.core.component.inject

internal class HomeStoreExecutor : BaseCoroutineExecutor<Intent, Action, State, Msg, Nothing>() {
    private val harFilesRepository: HARFilesRepository by inject()

    private val wearableRepository: WearableRepository by inject()

    private val bluetoothRepository: BluetoothRepository by inject()

    override fun executeIntent(intent: Intent, getState: () -> State) {
        when (intent) {
            is Intent.OnActivityResult -> onActivityResultIntent(intent.activityResult)
            is Intent.ScheduleFileData -> onScheduleFileDataIntent(
                device = intent.device,
                result = intent.result,
                context = intent.context
            )
            is Intent.DeviceClick -> onDeviceClick(intent.device)
            is Intent.Vibrate -> vibrate()
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
                                device = localDevice,
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
            readHARFile(uri)?.let {
                dispatchOnMainThread(
                    Msg.HRAFileData(
                        name = uri.lastPathSegment ?: "null",
                        data = it
                    )
                )
            }
        }
    )

    private suspend fun readHARFile(uri: Uri): WearableEvents? {
        when(val rResult = harFilesRepository.openHRAFile(uri)) {
            is BaseResult.Success -> return rResult.data
            is BaseResult.Failure -> Unit
        }

        return null
    }

    private fun onScheduleFileDataIntent(
        device: DevicesListItems.Device,
        result: ActivityResult,
        context: Context,
    ) {
        launchIO(
            safeAction = {
                result.data?.data?.let { uri ->
                    readHARFile(uri)?.let { events ->
                        wearableRepository.scheduleHRAFileData(device.device, context, events)
                    }
                }
            },
        )
    }

    private fun vibrate() {
        launchIO {
            bluetoothRepository.sendFindDeviceCommand(true)
        }
    }
}