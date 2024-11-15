package by.bashlikovvv.common.worker

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import java.util.UUID

class ForegroundServiceContract {
    private var service: ForegroundService? = null

    private var bound: Boolean = false

    private var binder: ForegroundService.LocalBinder? = null

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(
            name: ComponentName?,
            service: IBinder?,
        ) {
            binder = service as ForegroundService.LocalBinder
            this@ForegroundServiceContract.service = binder?.getService()
            bound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            bound = false
        }
    }

    fun bind(context: Context) {
        Intent(context, ForegroundService::class.java).also { intent ->
            context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    fun unbind(context: Context) {
        context.unbindService(connection)
    }

    fun trackWorker(context: Context, uuid: UUID) {
        bind(context)
        binder?.subscribeWork(uuid)
    }
}