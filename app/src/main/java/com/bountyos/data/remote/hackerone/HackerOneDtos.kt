package com.bountyos.data.remote.hackerone

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

/*
 * HackerOne Hacker API 的远程 DTO。
 *
 * HackerOne API 遵循 JSON:API 规范（jsonapi.org）。响应顶层为
 * `data` / `links`，资源由 `id`、`type`、`attributes`、`relationships`
 * 组成。字段采用 snake_case，这里通过 @SerialName 显式映射。
 *
 * `relationships` 内部的 `data` 可能是对象、数组或 null，且不同关系的
 * 具体类型不同，因此统一以 [JsonElement] 承载，在 mapper 中按需解析。
 */

@Serializable
data class HackerOneReportsResponse(
    val data: List<HackerOneReport> = emptyList(),
    val links: HackerOneLinks? = null,
)

@Serializable
data class HackerOneReportResponse(
    val data: HackerOneReport,
)

@Serializable
data class HackerOneLinks(
    val next: String? = null,
    val self: String? = null,
    val prev: String? = null,
)

@Serializable
data class HackerOneReport(
    val id: String,
    val type: String? = null,
    val attributes: HackerOneReportAttributes? = null,
    val relationships: HackerOneReportRelationships? = null,
)

@Serializable
data class HackerOneReportAttributes(
    val title: String? = null,
    val state: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("vulnerability_information") val vulnerabilityInformation: String? = null,
    @SerialName("triaged_at") val triagedAt: String? = null,
    @SerialName("closed_at") val closedAt: String? = null,
    @SerialName("last_activity_at") val lastActivityAt: String? = null,
    @SerialName("last_reporter_activity_at") val lastReporterActivityAt: String? = null,
    @SerialName("bounty_awarded_at") val bountyAwardedAt: String? = null,
    @SerialName("disclosed_at") val disclosedAt: String? = null,
)

@Serializable
data class HackerOneReportRelationships(
    val program: HackerOneRelationship? = null,
    val weakness: HackerOneRelationship? = null,
    val severity: HackerOneRelationship? = null,
    val bounties: HackerOneRelationship? = null,
)

@Serializable
data class HackerOneRelationship(
    val data: JsonElement? = null,
)

/* 内联子资源（relationships 的 data 具体解析目标）。 */

@Serializable
data class HackerOneProgram(
    val id: String? = null,
    val type: String? = null,
    val attributes: HackerOneProgramAttributes? = null,
)

@Serializable
data class HackerOneProgramAttributes(
    val handle: String? = null,
    val name: String? = null,
    val currency: String? = null,
)

@Serializable
data class HackerOneWeakness(
    val id: String? = null,
    val type: String? = null,
    val attributes: HackerOneWeaknessAttributes? = null,
)

@Serializable
data class HackerOneWeaknessAttributes(
    val name: String? = null,
    val description: String? = null,
    @SerialName("external_id") val externalId: String? = null,
)

@Serializable
data class HackerOneSeverity(
    val id: String? = null,
    val type: String? = null,
    val attributes: HackerOneSeverityAttributes? = null,
)

@Serializable
data class HackerOneSeverityAttributes(
    val rating: String? = null,
)

@Serializable
data class HackerOneBounty(
    val id: String? = null,
    val type: String? = null,
    val attributes: HackerOneBountyAttributes? = null,
)

@Serializable
data class HackerOneBountyAttributes(
    val amount: String? = null,
    @SerialName("bonus_amount") val bonusAmount: String? = null,
    @SerialName("awarded_amount") val awardedAmount: String? = null,
    @SerialName("awarded_currency") val awardedCurrency: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
)
