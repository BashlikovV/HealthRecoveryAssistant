package by.bashlikovvv.bluetooth.model

/**
 * Groups a bunch of {@link BtLEAction actions} together, making sure
 * that upon failure of one action, all subsequent actions are discarded.
 *
 * @author TREND
 */
class Transaction {
    val taskName: String

    val creationTimestamp: Long

    private val _actions: MutableList<BtLEAction> = mutableListOf()
    val actions: List<BtLEAction> get() = _actions

    var gattCallback: GattCallback? = null
        private set

    private var modifyGattCallback: Boolean = false

    constructor(taskName: String) {
        this.taskName = taskName
        creationTimestamp = System.currentTimeMillis()
    }

    fun add(action: BtLEAction) {
        _actions.add(action)
    }

    fun isEmpty() = _actions.isEmpty()

    fun setCallback(callback: GattCallback) {
        this.gattCallback = callback
        modifyGattCallback = true
    }

    fun isModifyGattCallback() = modifyGattCallback

    fun getActionCount() = _actions.size
}