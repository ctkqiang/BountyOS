package com.bountyos.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 报告活动（时间线事件）的本地缓存实体。
 */
@Entity(tableName = "activities")
data class ActivityEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "provider")
    val provider: String,
    @ColumnInfo(name = "submission_external_id")
    val submissionExternalId: String,
    @ColumnInfo(name = "actor")
    val actor: String?,
    @ColumnInfo(name = "type")
    val type: String,
    @ColumnInfo(name = "message")
    val message: String?,
    @ColumnInfo(name = "timestamp")
    val timestamp: Long?,
)
