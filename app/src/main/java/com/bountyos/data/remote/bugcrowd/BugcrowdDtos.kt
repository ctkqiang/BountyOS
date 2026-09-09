package com.bountyos.data.remote.bugcrowd

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/*
 * Bugcrowd API 的远程 DTO。
 *
 * Bugcrowd API 采用 JSON:API 风格（`data.attributes`），鉴权为
 * `Authorization: Token`，媒体类型为 `application/vnd.bugcrowd+json`。
 *
 * 字段名以官方文档 / 官方调用示例（`fields[submission]=...`）确认
 * 的为准；未确认的时间字段不建模，避免臆造。
 */

@Serializable
data class BugcrowdSubmissionsResponse(
    val data: List<BugcrowdSubmission> = emptyList(),
)

@Serializable
data class BugcrowdSubmissionResponse(
    val data: BugcrowdSubmission,
)

@Serializable
data class BugcrowdSubmission(
    val id: String,
    val type: String? = null,
    val attributes: BugcrowdSubmissionAttributes? = null,
)

@Serializable
data class BugcrowdSubmissionAttributes(
    val title: String? = null,
    val state: String? = null,
    /** Bugcrowd 优先级，官方未定义与 low/medium/high/critical 的映射。 */
    val severity: String? = null,
    @SerialName("bug_url") val bugUrl: String? = null,
    @SerialName("vrt_id") val vrtId: String? = null,
    val description: String? = null,
    val source: String? = null,
    @SerialName("remediation_advice") val remediationAdvice: String? = null,
)
