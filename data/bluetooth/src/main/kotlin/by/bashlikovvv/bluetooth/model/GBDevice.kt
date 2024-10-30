package by.bashlikovvv.bluetooth.model

import android.bluetooth.BluetoothDevice

data class GBDevice(
    val device: BluetoothDevice,
    val deviceType: DeviceType,
) {
    private var state = State.NOT_CONNECTED

    val isInitialized: Boolean
        get() = state == State.SCANNED || state.equalsOrHigherThan(State.INITIALIZED)

    val isConnected: Boolean
        get() = state == State.CONNECTED

    fun setState(state: State) {
        this.state = state
    }

    enum class State {
        NOT_CONNECTED,
        WAITING_FOR_RECONNECT,
        WAITING_FOR_SCAN,
        SCANNED,
        CONNECTING,
        CONNECTED,
        INITIALIZING,
        AUTHENTICATION_REQUIRED,
        AUTHENTICATING,
        INITIALIZED;

        fun equalsOrHigherThan(other: State): Boolean {
            return compareTo(other) >= 0
        }
    }
}