package by.bashlikovvv.discovery.domain.contract

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.ParcelUuid
import by.bashlikovvv.discovery.domain.model.BluetoothAction

class BluetoothReceiver(
    private val onAction: (BluetoothAction) -> Unit
) : BroadcastReceiver() {
    private val actions: Map<String, ((Intent) -> Unit)?> = mapOf(
        BluetoothAction.DiscoveryStarted.NAME to { onAction(BluetoothAction.DiscoveryStarted) },
        BluetoothAction.StateChanged.NAME to { intent -> onStateChangedAction(intent) },
        BluetoothAction.Found.NAME to { intent -> onFoundAction(intent) },
        BluetoothAction.UUID.NAME to { intent -> onUUIDAction(intent) },
        BluetoothAction.BondStateChanged.NAME to { intent -> onBondStateChangedAction(intent) }
    )

    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.action
            ?.let { actions.getOrElse(it) { null } }
            ?.invoke(intent)
    }

    private fun onBondStateChangedAction(intent: Intent) {
        onAction(
            BluetoothAction.BondStateChanged(
                device = getDevice(intent)
            )
        )
    }

    private fun onUUIDAction(intent: Intent) {
        onAction(
            BluetoothAction.UUID(
                device = getDevice(intent), rssi = intent.getShortExtra(
                    BluetoothDevice.EXTRA_RSSI, BluetoothAction.Found.DEFAULT_RSSI
                ).let { rssi ->
                    if (rssi == BluetoothAction.Found.DEFAULT_RSSI) {
                        null
                    } else {
                        rssi
                    }
                }, uuids = getParcelUuids(intent)
            )
        )
    }

    private fun onFoundAction(intent: Intent) {
        onAction(BluetoothAction.Found(device = getDevice(intent), rssi = intent.getShortExtra(
            BluetoothDevice.EXTRA_RSSI, BluetoothAction.Found.DEFAULT_RSSI
        ).let { rssi ->
            if (rssi == BluetoothAction.Found.DEFAULT_RSSI) {
                null
            } else {
                rssi
            }
        }))
    }

    private fun onStateChangedAction(intent: Intent) {
        onAction(
            BluetoothAction.StateChanged(
                state = intent.getIntExtra(
                    BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF
                )
            )
        )
    }

    private fun getDevice(intent: Intent): BluetoothDevice? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE, BluetoothDevice::class.java)
        } else {
            intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
        }
    }

    @Suppress("DEPRECATION")
    private fun getParcelUuids(intent: Intent): Array<ParcelUuid>? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableArrayExtra(
                BluetoothDevice.EXTRA_UUID, Array<ParcelUuid>::class.java
            )
        } else {
            intent.getParcelableArrayExtra(BluetoothDevice.EXTRA_UUID)
        }.let { it as Array<ParcelUuid> }
    }
}