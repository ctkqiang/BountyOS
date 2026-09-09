package com.bountyos.domain.repository

import com.bountyos.domain.model.Integration
import com.bountyos.domain.model.Provider
import kotlinx.coroutines.flow.Flow

/**
 * 平台集成的管理契约。
 *
 * 负责连接（保存并校验凭证）、断开（清除凭证）以及观察连接状态。
 * 凭证的具体存储方式对上层透明。
 */
interface IntegrationRepository {

    fun observeIntegrations(): Flow<List<Integration>>

    /**
     * 连接某个平台。
     *
     * 流程：保存凭证 -> 调用平台校验 -> 校验通过则标记已连接，
     * 校验失败则清除凭证并返回失败结果。
     *
     * @param username HackerOne 需要用户名；Bugcrowd 传 null。
     * @param token    平台访问令牌。
     */
    suspend fun connect(provider: Provider, username: String?, token: String): Result<Unit>

    /** 断开某个平台，清除本地凭证。 */
    suspend fun disconnect(provider: Provider)
}
