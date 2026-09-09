package com.bountyos.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 每个平台的分页同步游标。
 *
 * 用于增量同步：记录上一次同步到哪个游标，以便下次从该位置继续，
 * 避免重复拉取。游标的具体含义由各平台适配器定义（HackerOne 为
 * `links.next` URL，Bugcrowd 为页码）。
 */
@Entity(tableName = "sync_state")
data class SyncStateEntity(
    @PrimaryKey
    @ColumnInfo(name = "provider")
    val provider: String,
    @ColumnInfo(name = "cursor")
    val cursor: String?,
    @ColumnInfo(name = "last_synced_at")
    val lastSyncedAt: Long?,
)
