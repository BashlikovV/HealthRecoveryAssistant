package by.bashlikovvv.bluetooth.devices.huami

interface Huami2021Handler {
    fun handle2021Payload(type: Short, payload: ByteArray)
}