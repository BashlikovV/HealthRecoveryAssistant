package by.bashlikovvv.bluetooth.model

abstract class AbstractTransaction {
    val taskName: String

    val creationTimestamp: Long

    constructor(taskName: String) {
        this.taskName = taskName
        creationTimestamp = System.currentTimeMillis()
    }

    abstract fun getActionCount(): Int
}