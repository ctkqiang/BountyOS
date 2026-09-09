package com.bountyos.data.remote.hackerone

import com.bountyos.data.remote.ApiJson
import com.bountyos.domain.model.Provider
import com.bountyos.domain.model.Reward
import com.bountyos.domain.model.Submission
import com.bountyos.domain.model.Weakness
import com.bountyos.domain.normalization.StatusNormalizer
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.time.Instant

/*
 * HackerOne DTO -> 领域模型的映射。
 *
 * `relationships.data` 以 JsonElement 承载，这里按关系类型解析为
 * 具体 DTO。severity 的字段名（rating）尚未在官方文档中完全确认，
 * 解析失败时返回 UNKNOWN 并保留原始值，避免猜测。
 */

internal fun HackerOneReport.toSubmission(): Submission {
    val attributes = attributes
    val relationships = relationships

    val program = relationships?.program?.data
        ?.let { element ->
            runCatching { ApiJson.decodeFromJsonElement(HackerOneProgram.serializer(), element) }
                .getOrNull()
        }
    val weakness = relationships?.weakness?.data
        ?.let { element ->
            runCatching { ApiJson.decodeFromJsonElement(HackerOneWeakness.serializer(), element) }
                .getOrNull()
        }
    val severityRating = relationships?.severity?.data
        ?.jsonObject?.get("attributes")
        ?.jsonObject?.get("rating")
        ?.jsonPrimitive?.content
    val bounties = relationships?.bounties?.data
        ?.let { decodeBounties(it) }
        .orEmpty()

    return Submission(
        id = submissionId(Provider.HACKERONE, id),
        provider = Provider.HACKERONE,
        externalId = id,
        programName = program?.attributes?.name ?: program?.attributes?.handle,
        title = attributes?.title.orEmpty(),
        status = StatusNormalizer.fromHackerOne(attributes?.state),
        providerStatus = attributes?.state.orEmpty(),
        severity = StatusNormalizer.hackerOneSeverity(severityRating),
        providerSeverity = severityRating,
        weakness = weakness?.attributes?.let { attrs ->
            Weakness(
                name = attrs.name.orEmpty(),
                cweId = attrs.externalId,
                description = attrs.description,
            )
        },
        submittedAt = attributes?.createdAt?.toInstant(),
        updatedAt = attributes?.lastActivityAt?.toInstant(),
        resolvedAt = attributes?.closedAt?.toInstant(),
        reward = bounties.firstOrNull()?.toReward(),
        canonicalUrl = "https://hackerone.com/reports/$id",
        vulnerabilityInformation = attributes?.vulnerabilityInformation,
    )
}

internal fun submissionId(provider: Provider, externalId: String): String =
    "${provider.name}:$externalId"

private fun decodeBounties(data: JsonElement): List<HackerOneBounty> =
    runCatching {
        when (data) {
            is JsonArray -> data.map { element ->
                ApiJson.decodeFromJsonElement(HackerOneBounty.serializer(), element)
            }
            else -> listOf(ApiJson.decodeFromJsonElement(HackerOneBounty.serializer(), data))
        }
    }.getOrElse { emptyList() }

private fun HackerOneBounty.toReward(): Reward {
    val attributes = attributes ?: return Reward(amount = "0", currency = null)
    val currency = attributes.awardedCurrency
    val amount = attributes.awardedAmount ?: attributes.amount ?: "0"
    return Reward(amount = amount, currency = currency)
}

private fun String?.toInstant(): Instant? =
    this?.let { value -> runCatching { Instant.parse(value) }.getOrNull() }
