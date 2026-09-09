package com.bountyos.domain.model

/**
 * 平台中立的提交（漏洞报告）领域模型。
 *
 * 该模型不携带任何 HackerOne 或 Bugcrowd 的 DTO 类型，UI 层与
 * Repository 层均以此模型作为契约。它由各 provider 适配器在读取
 * 远程数据后通过 mapper 转换得到。
 */
data class Submission(
    /** 本地唯一标识，由 `provider + externalId` 组合得到。 */
    val id: String,
    /** 来源平台。 */
    val provider: Provider,
    /** 平台内部的报告编号，例如 HackerOne 的 "182931"。 */
    val externalId: String,
    /** 所属项目名称（若可得）。 */
    val programName: String?,
    /** 报告标题。 */
    val title: String,
    /** 规范化状态。 */
    val status: SubmissionStatus,
    /** 平台原始状态值，例如 "needs-more-info"。 */
    val providerStatus: String,
    /** 规范化严重程度（Bugcrowd 通常为 [Severity.UNKNOWN]）。 */
    val severity: Severity?,
    /** 平台原始严重程度，例如 "critical" 或 "P1"。 */
    val providerSeverity: String?,
    /** 漏洞类型。 */
    val weakness: Weakness?,
    /** 提交时间。 */
    val submittedAt: java.time.Instant?,
    /** 最近更新时间。 */
    val updatedAt: java.time.Instant?,
    /** 解决/关闭时间。 */
    val resolvedAt: java.time.Instant?,
    /** 已获得的奖励（若有）。 */
    val reward: Reward?,
    /** 平台报告页的原始 URL（用于「在平台中打开」）。 */
    val canonicalUrl: String?,
    /** 漏洞信息正文（Markdown 文本，未解析的原始内容）。 */
    val vulnerabilityInformation: String?,
)
