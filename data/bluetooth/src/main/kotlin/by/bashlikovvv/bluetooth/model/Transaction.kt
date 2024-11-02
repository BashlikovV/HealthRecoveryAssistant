package by.bashlikovvv.bluetooth.model

class Transaction(
    taskName: String,
) : AbstractTransaction(taskName) {
    private val _actions = mutableListOf<BtLEAction>()
    val actions: List<BtLEAction> get() = _actions

    var callback: GattCallback? = null
        private set

    var isGattCallbackModified: Boolean = false
        private set

    constructor(taskName: String, callback: GattCallback) : this(taskName) {
        this.callback = callback
    }

    fun setCallback(callback: GattCallback) {
        this.callback = callback
        isGattCallbackModified = true
    }

    fun add(action: BtLEAction) {
        _actions.add(action)
    }

    override fun getActionCount(): Int = _actions.size
}