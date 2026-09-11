package com.bountyos.sync

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 后台同步的调度器。
 *
 * 以较低的周期（6 小时）调度同步，避免频繁轮询从而尊重平台
 * 速率限制。WorkManager 会对周期任务做系统级约束（网络、电量）。
 */
@Singleton
class SyncScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    fun schedulePeriodicSync() {
        val request = PeriodicWorkRequestBuilder<SyncWorker>(SYNC_INTERVAL_HOURS, TimeUnit.HOURS).build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            UNIQUE_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request,
        )
    }

    fun scheduleExploitDbSync() {
        val request = PeriodicWorkRequestBuilder<ExploitDbSyncWorker>(
            EXPLOITDB_INTERVAL_HOURS,
            TimeUnit.HOURS,
        ).build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            EXPLOITDB_UNIQUE_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request,
        )
    }

    private companion object {
        const val UNIQUE_WORK_NAME = "bountyos_periodic_sync"
        const val SYNC_INTERVAL_HOURS = 1L
        const val EXPLOITDB_UNIQUE_WORK_NAME = "bountyos_exploitdb_sync"
        const val EXPLOITDB_INTERVAL_HOURS = 12L
    }
}
