package com.bountyos.domain.repository

import com.bountyos.domain.model.Provider

/**
 * 同步结果。
 *
 * [Success] 表示所有已连接平台均同步成功；[Failed] 表示至少一个
 * 平台同步失败，`failures` 记录每个失败平台及其原因。
 */
sealed interface SyncResult {

    data object Success : SyncResult

    data class Failed(
        val failures: Map<Provider, Throwable>,
    ) : SyncResult
}
