package com.bountyos.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 提交（漏洞报告）的本地缓存实体。
 *
 * 与领域模型 [com.bountyos.domain.model.Submission] 一一对应，但字段
 * 采用数据库友好的扁平结构：枚举以 `name` 字符串存储，时间以
 * epoch 毫秒存储，避免在 Room 中引入复杂的类型转换器。
 *
 * 跨平台唯一性由 `(provider, external_id)` 联合唯一索引保证——
 * 不同平台可能使用相同的数字 ID。
 */
@Entity(
    tableName = "submissions",
    indices = [Index(value = ["provider", "external_id"], unique = true)],
)
data class SubmissionEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "provider")
    val provider: String,
    @ColumnInfo(name = "external_id")
    val externalId: String,
    @ColumnInfo(name = "program_name")
    val programName: String?,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "status")
    val status: String,
    @ColumnInfo(name = "provider_status")
    val providerStatus: String,
    @ColumnInfo(name = "severity")
    val severity: String?,
    @ColumnInfo(name = "provider_severity")
    val providerSeverity: String?,
    @ColumnInfo(name = "weakness_name")
    val weaknessName: String?,
    @ColumnInfo(name = "weakness_cwe_id")
    val weaknessCweId: String?,
    @ColumnInfo(name = "weakness_description")
    val weaknessDescription: String?,
    @ColumnInfo(name = "submitted_at")
    val submittedAt: Long?,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long?,
    @ColumnInfo(name = "resolved_at")
    val resolvedAt: Long?,
    @ColumnInfo(name = "reward_amount")
    val rewardAmount: String?,
    @ColumnInfo(name = "reward_currency")
    val rewardCurrency: String?,
    @ColumnInfo(name = "canonical_url")
    val canonicalUrl: String?,
    @ColumnInfo(name = "vulnerability_information")
    val vulnerabilityInformation: String?,
)
