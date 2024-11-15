package by.bashlikovvv.root.presentation.ui.store

import android.Manifest
import android.content.Context
import by.bashlikovvv.common.repository.RootRepository
import by.bashlikovvv.root.presentation.ui.store.RootStore.Intent
import by.bashlikovvv.root.presentation.ui.store.RootStore.Label
import by.bashlikovvv.root.presentation.ui.store.RootStore.State
import by.bashlikovvv.root.presentation.ui.store.RootStoreFactory.Action
import by.bashlikovvv.root.presentation.ui.store.RootStoreFactory.Msg
import by.bashlikovvv.ui.base.BaseCoroutineExecutor
import by.bashlikovvv.ui.base.PermissionsController
import kotlinx.coroutines.delay
import org.koin.core.component.inject

internal class RootStoreExecutor : BaseCoroutineExecutor<Intent, Action, State, Msg, Label>() {
    private val rootRepository: RootRepository by inject()

    private val requiredPermissions = mutableMapOf<String, Boolean>()

    private val permissionsController = PermissionsController(
        callbacks = object : PermissionsController.Callbacks {
            override fun requestPermission(permission: String) {
                launchMain {
                    publish(Label.RequestPermission(permission))
                }
            }

            override fun onPermissionResult(permission: String, granted: Boolean) {
                requiredPermissions[permission] = granted
                if (requiredPermissions.all { (_, granted) -> granted }) {
                    launchMain { publish(Label.StartForegroundService) }
                }
            }
        }
    )

    override fun executeIntent(intent: Intent, getState: () -> State) {
        when (intent) {
            is Intent.Initialize -> initialize(intent.context)
            is Intent.Destroy -> destroy()
            is Intent.OnNewIntent -> onNewIntent(intent.intent)
            is Intent.OnPermissionResult -> onPermissionResult(intent.permission, intent.granted)
        }
    }

    override fun executeAction(action: Action, getState: () -> State) {
        when(action) {
            is Action.Initialize -> requestPermission(action.permissions)
        }
    }

    private fun initialize(context: Context) = launchIO(
        safeAction = { rootRepository.initialize(context) },
        onError = { th: Throwable -> dispatch(Msg.Error(th)) }
    )

    private fun onNewIntent(intent: android.content.Intent) {
        if (intent.action == android.content.Intent.ACTION_VIEW) {
            intent.data?.let { publish(Label.OpenHARFile(it)) }
        }
    }

    private fun destroy() {
        rootRepository.destroy()
    }

    private fun onPermissionResult(permission: String, granted: Boolean) {
        permissionsController.onPermissionResult(permission, granted)
    }

    private fun requestPermission(permissions: List<String>) {
        launchIO {
            delay(750)
            permissions.forEach { requiredPermissions[it] = false }
            permissionsController.requestPermissions(permissions)
        }
    }
}