package com.bountyos.domain.model

/**
 * 平台中立的报告活动（时间线事件）。
 *
 * 活动是报告上的一次事件：状态变更、评论、奖励发放等。不同平台对
 * 活动类型的命名不同，因此 [type] 保留平台的原始活动类型字符串。
 */
data class Activity(
    /** 本地唯一标识。 */
    val id: String,
    /** 来源平台。 */
    val provider: Provider,
    /** 所属报告的平台内部编号。 */
    val submissionExternalId: String,
    /** 活动执行者（用户或项目团队），可能为空表示匿名。 */
    val actor: String?,
    /** 平台原始活动类型，例如 "status-change"。 */
    val type: String,
    /** 活动正文 / 评论内容。 */
    val message: String?,
    /** 活动发生时间。 */
    val timestamp: java.time.Instant?,
)
