package com.bountyos.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 平台集成的本地状态。
 *
 * 该实体只存储**非敏感**的连接元数据：是否已连接、最近同步时间。
 * 凭证本身由 [com.bountyos.data.security.CredentialStore] 通过
 * Android Keystore 加密保存，绝不进入 Room。
 */
@Entity(tableName = "integrations")
data class IntegrationEntity(
    @PrimaryKey
    @ColumnInfo(name = "provider")
    val provider: String,
    @ColumnInfo(name = "connected")
    val connected: Boolean,
    @ColumnInfo(name = "last_synced_at")
    val lastSyncedAt: Long?,
)
