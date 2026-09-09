package com.bountyos

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * BountyOS 应用入口。
 *
 * 通过 Hilt 建立依赖注入图，并配置 WorkManager 使用 Hilt 的
 * WorkerFactory，使 [com.bountyos.sync.SyncWorker] 能注入依赖。
 */
@HiltAndroidApp
class BountyOsApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}
