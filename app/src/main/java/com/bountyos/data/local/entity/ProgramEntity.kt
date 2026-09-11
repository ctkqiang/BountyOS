package com.bountyos.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 项目（Program）的本地缓存实体。
 *
 * 以 `(provider, handle)` 作为跨平台唯一约束：同一平台内句柄唯一。
 */
@Entity(
    tableName = "programs",
    indices = [Index(value = ["provider", "handle"], unique = true)],
)
data class ProgramEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "provider")
    val provider: String,
    @ColumnInfo(name = "external_id")
    val externalId: String?,
    @ColumnInfo(name = "handle")
    val handle: String,
    @ColumnInfo(name = "name")
    val name: String?,
    @ColumnInfo(name = "is_following")
    val isFollowing: Boolean = false,
)
