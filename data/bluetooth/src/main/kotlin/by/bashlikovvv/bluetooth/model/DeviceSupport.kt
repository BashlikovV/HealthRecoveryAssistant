package by.bashlikovvv.bluetooth.model

interface DeviceSupport {
    fun connect(): Boolean

    fun setReminders(reminders: List<Reminder>)

    fun onFindDevice(start: Boolean)
}