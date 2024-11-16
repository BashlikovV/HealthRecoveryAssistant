package by.bashlikovvv.discovery.presentation.ui.store

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.os.ParcelUuid
import by.bashlikovvv.common.repository.BluetoothRepository
import by.bashlikovvv.discovery.domain.model.BluetoothAction
import by.bashlikovvv.discovery.domain.model.BluetoothState
import by.bashlikovvv.discovery.domain.model.BondAction
import by.bashlikovvv.discovery.domain.model.DiscoveryListItems.Device
import by.bashlikovvv.discovery.presentation.ui.store.DiscoveryStore.*
import by.bashlikovvv.discovery.presentation.ui.store.DiscoveryStoreFactory.*
import by.bashlikovvv.domain.base.BaseResult
import by.bashlikovvv.domain.model.BluetoothService
import by.bashlikovvv.ui.base.BaseCoroutineExecutor
import by.bashlikovvv.ui.base.PermissionsController
import by.bashlikovvv.util.ext.deviceName
import kotlinx.coroutines.delay
import org.koin.core.component.inject

internal class DiscoveryStoreExecutor : BaseCoroutineExecutor<Intent, Action, State, Msg, Label>() {
    private val bluetoothService: BluetoothService by inject()

    private val bluetoothRepository: BluetoothRepository by inject()

    private val permissionsController = PermissionsController(
        callbacks = object : PermissionsController.Callbacks {
            override fun requestPermission(permission: String) {
                launchMain {
                    publish(Label.RequestPermission(permission))
                }
            }

            override fun onPermissionResult(permission: String, granted: Boolean) {}
        }
    )

    override fun executeIntent(intent: Intent, getState: () -> State) {
        when (intent) {
            is Intent.DiscoveryButtonClicked -> onDiscoveryButtonClicked(getState())
            is Intent.OnBluetoothAction -> onBluetoothAction(intent)
            is Intent.StartDiscovery -> startDiscovery()
            is Intent.BondDevice -> onBondIntent(intent.address)
            is Intent.OnBondAction -> onBondAction(intent.action)
            is Intent.OnPermissionResult -> onPermissionResult(intent.permission, intent.granted)
        }
    }

    override fun executeAction(action: Action, getState: () -> State) {
        when (action) {
            is Action.Initialize -> requestPermission(action.permissions)
        }
    }

    private fun onPermissionResult(permission: String, granted: Boolean) {
        permissionsController.onPermissionResult(permission, granted)
    }

    private fun requestPermission(permissions: List<String>) {
        launchIO {
            delay(750)
            permissionsController.requestPermissions(permissions)
        }
    }

    private fun cancelDiscovery() {
        when (bluetoothService.cancelDiscovery()) {
            is BaseResult.Success -> {
                dispatch(Msg.Discover(false))
                publish(Label.CancelDiscovery)
            }

            is BaseResult.Failure -> Unit
        }
    }

    private fun startDiscovery() {
        when (bluetoothService.startDiscovery()) {
            is BaseResult.Success -> {
                dispatch(Msg.Discover(true))
                publish(Label.StartDiscovery)
            }

            is BaseResult.Failure -> Unit
        }
    }

    private fun onBondAction(bondAction: BondAction) {
        when (bondAction) {
            is BondAction.Bonded -> {
                dispatch(Msg.DeviceBonded(bondAction.device.address))
                launchIO { bluetoothRepository.connectFirstTime(device = bondAction.device) }
            }

            is BondAction.Bonding -> dispatch(Msg.DeviceBonding(bondAction.device.address))
            is BondAction.None -> dispatch(Msg.DeviceError(bondAction.device.address))
            is BondAction.Default -> {}
        }
    }

    private fun onBondIntent(address: String) {
        cancelDiscovery()
        bluetoothService.bondDevice(address)
        bluetoothService.getBluetoothDeviceByAddress(address)?.let {
            launchIO { bluetoothRepository.connectFirstTime(it) }
        }
    }

    private fun onDiscoveryButtonClicked(state: State) {
        val isScanning = !state.isScanning
        if (isScanning) {
            if (!bluetoothService.bluetoothEnabled) {
                publish(Label.TurnOnBluetooth)
                return
            }
            dispatch(Msg.Discover(true))
            startDiscovery()
            bluetoothService.getBoundDevices().forEach { device ->
                if (bluetoothService.addBluetoothDevice(device)) {
                    addDevice(device, true)
                }
            }
        } else {
            cancelDiscovery()
        }
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
        val state = when (state) {
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
        device?.let { deviceNotNull ->
            if (bluetoothService.addBluetoothDevice(deviceNotNull, rssi, uuids)) {
                addDevice(deviceNotNull)
            }
        }
    }

    private fun handleDeviceBonded(device: BluetoothDevice?) {
        device?.let { deviceNotNull ->
            if (bluetoothService.addBluetoothDevice(deviceNotNull)) {
                addDevice(deviceNotNull)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun addDevice(
        device: BluetoothDevice,
        isBonded: Boolean = false,
    ) {
        device.deviceName?.let { deviceNotNull ->
            dispatch(
                Msg.AddDevice(
                    Device(
                        id = device.hashCode(),
                        name = deviceNotNull,
                        address = device.address,
                        isBonded = isBonded
                    )
                )
            )
        }
    }
}