package by.bashlikovvv.ui.base

import java.util.concurrent.CountDownLatch
import java.util.concurrent.LinkedBlockingQueue
import kotlin.concurrent.thread

class PermissionsController {
    private var isDisposed: Boolean = false

    private var isCrashed: Boolean = false

    private val permissionsQueue = LinkedBlockingQueue<String>()

    private var countDownLatch: CountDownLatch? = null

    private var lastPermission: String? = null

    private val callbacks: Callbacks

    private val thread = thread(start = false) {
        try {
            while (!isDisposed && !isCrashed) {
                val permission = permissionsQueue.take()
                lastPermission = permission
                callbacks.requestPermission(permission)
                countDownLatch = CountDownLatch(1)
                countDownLatch?.await()
                countDownLatch = null
            }
        } catch (_: Exception) {
            isCrashed = true
        } finally {
            countDownLatch = null
        }
    }

    constructor(callbacks: Callbacks) {
        this.callbacks = callbacks
        thread.start()
    }

    fun requestPermissions(permissions: List<String>) {
        permissionsQueue.addAll(permissions)
    }

    fun onPermissionResult(
        permission: String,
        granted: Boolean
    ) {
        callbacks.onPermissionResult(permission, granted)
        if (lastPermission == permission) {
            countDownLatch?.countDown()
        }
    }

    interface Callbacks {
        fun requestPermission(permission: String)

        fun onPermissionResult(permission: String, granted: Boolean)
    }
}