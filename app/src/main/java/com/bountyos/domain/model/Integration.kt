package com.bountyos.domain.model

import java.time.Instant

/**
 * 平台集成状态（非敏感元数据）。
 *
 * 仅表示「是否已连接」与「最近同步时间」。凭证本身不在此模型中，
 * 由安全存储单独管理。
 */
data class Integration(
    val provider: Provider,
    val connected: Boolean,
    val lastSyncedAt: Instant?,
)
