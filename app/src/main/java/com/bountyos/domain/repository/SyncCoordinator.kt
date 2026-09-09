package com.bountyos.domain.repository

import com.bountyos.domain.model.Provider

/**
 * 同步协调器契约。
 *
 * 负责从已连接的平台拉取数据并写入本地缓存，同时检测数据变化以
 * 触发本地通知。同步是只读的，绝不向平台写入任何数据。
 */
interface SyncCoordinator {

    /** 同步所有已连接平台。 */
    suspend fun synchronize(): SyncResult

    /** 同步指定平台。 */
    suspend fun synchronize(provider: Provider): SyncResult
}
