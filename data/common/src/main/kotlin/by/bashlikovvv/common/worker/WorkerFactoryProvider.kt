package by.bashlikovvv.common.worker

import androidx.work.WorkerFactory

interface WorkerFactoryProvider {
    fun provideFactory(): WorkerFactory?

    object Base : WorkerFactoryProvider {
        override fun provideFactory(): WorkerFactory? = null
    }
}