package by.bashlikovvv.discovery.presentation.ui.store

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.os.ParcelUuid
import by.bashlikovvv.discovery.domain.model.BluetoothAction
import by.bashlikovvv.discovery.domain.model.BluetoothState
import by.bashlikovvv.discovery.domain.model.DiscoveryListItems.Device
import by.bashlikovvv.discovery.presentation.ui.store.DiscoveryStore.*
import by.bashlikovvv.discovery.presentation.ui.store.DiscoveryStoreFactory.*
import by.bashlikovvv.domain.model.BluetoothService
import by.bashlikovvv.ui.base.BaseCoroutineExecutor
import by.bashlikovvv.util.deviceName
import org.koin.core.component.inject

internal class DiscoveryStoreExecutor : BaseCoroutineExecutor<Intent, Action, State, Msg, Label>() {
    private val bluetoothService: BluetoothService by inject()

    override fun executeIntent(intent: Intent, getState: () -> State) {
        when (intent) {
            is Intent.ConnectDevice -> {}
            is Intent.DiscoveryButtonClicked -> onDiscoveryButtonClicked(getState())
            is Intent.ShowDialog -> showDialog(intent)
            is Intent.OnBluetoothAction -> onBluetoothAction(intent)
        }
    }

    private fun onDiscoveryButtonClicked(state: State) {
        val isScanning = !state.isScanning
        dispatch(Msg.Discover(isScanning))
        if (isScanning) {
            bluetoothService.startDiscovery()
            bluetoothService.getBoundDevices().forEach { device -> addDevice(device) }
        } else {
            bluetoothService.cancelDiscovery()
        }
    }

    private fun showDialog(intent: Intent.ShowDialog) {
        publish(
            Label.ShowDialog(
                title = intent.title,
                text = intent.text,
                confirmButton = intent.confirmButton,
                dismissButton = intent.dismissButton,
            )
        )
    }

    private fun onBluetoothAction(intent: Intent.OnBluetoothAction) {
        when (val action = intent.action) {
            is BluetoothAction.DiscoveryStarted -> Unit
            is BluetoothAction.StateChanged -> bluetoothStateChanged(action.state)
            is BluetoothAction.Found -> scheduleProcessing(
                device = action.device,
                rssi = action.rssi,
                uuids = null
            )
            is BluetoothAction.UUID -> scheduleProcessing(
                device = action.device,
                rssi = action.rssi,
                uuids = action.uuids
            )
            is BluetoothAction.BondStateChanged -> handleDeviceBonded(action.device)
        }
    }

    private fun bluetoothStateChanged(state: Int) {
        val state = when(state) {
            BluetoothAdapter.STATE_ON -> BluetoothState.On
            BluetoothAdapter.STATE_TURNING_ON -> BluetoothState.TurningOn
            BluetoothAdapter.STATE_OFF -> BluetoothState.Off
            BluetoothAdapter.STATE_TURNING_OFF -> BluetoothState.TurningOff
            else -> BluetoothState.Unknown
        }
        dispatch(Msg.ChangeBluetoothState(state))
    }

    private fun scheduleProcessing(
        device: BluetoothDevice?,
        rssi: Short?,
        uuids: Array<ParcelUuid>?,
    ) {
        device?.let { deviceNotNull -> addDevice(deviceNotNull) }
    }

    private fun handleDeviceBonded(device: BluetoothDevice?) {
        device?.let { deviceNotNull -> addDevice(deviceNotNull) }
    }

    @SuppressLint("MissingPermission")
    private fun addDevice(device: BluetoothDevice) {
        device.deviceName?.let { deviceNotNull ->
            dispatch(
                Msg.AddDevice(
                    Device(
                        id = device.hashCode(),
                        name = deviceNotNull,
                        address = device.address,
                    )
                )
            )
        }
    }
}