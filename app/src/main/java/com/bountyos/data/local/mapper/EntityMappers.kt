package com.bountyos.data.local.mapper

import com.bountyos.data.local.entity.ActivityEntity
import com.bountyos.data.local.entity.ProgramEntity
import com.bountyos.data.local.entity.SubmissionEntity
import com.bountyos.domain.model.Activity
import com.bountyos.domain.model.Program
import com.bountyos.domain.model.Provider
import com.bountyos.domain.model.Reward
import com.bountyos.domain.model.Severity
import com.bountyos.domain.model.Submission
import com.bountyos.domain.model.SubmissionStatus
import com.bountyos.domain.model.Weakness
import java.time.Instant

/*
 * 本地实体 <-> 领域模型的集中映射。
 *
 * 领域层使用强类型枚举与 [Instant]，数据库层使用字符串与 epoch 毫秒。
 * 所有转换都集中在此处，避免散落到 ViewModel 或 Composable 中。
 */

internal fun SubmissionEntity.toDomain(): Submission = Submission(
    id = id,
    provider = provider.toProvider(),
    externalId = externalId,
    programName = programName,
    title = title,
    status = status.toStatus(),
    providerStatus = providerStatus,
    severity = severity?.toSeverity(),
    providerSeverity = providerSeverity,
    weakness = toWeakness(),
    submittedAt = submittedAt.toInstant(),
    updatedAt = updatedAt.toInstant(),
    resolvedAt = resolvedAt.toInstant(),
    reward = toReward(),
    canonicalUrl = canonicalUrl,
    vulnerabilityInformation = vulnerabilityInformation,
)

internal fun Submission.toEntity(): SubmissionEntity = SubmissionEntity(
    id = id,
    provider = provider.name,
    externalId = externalId,
    programName = programName,
    title = title,
    status = status.name,
    providerStatus = providerStatus,
    severity = severity?.name,
    providerSeverity = providerSeverity,
    weaknessName = weakness?.name,
    weaknessCweId = weakness?.cweId,
    weaknessDescription = weakness?.description,
    submittedAt = submittedAt.toEpochMillis(),
    updatedAt = updatedAt.toEpochMillis(),
    resolvedAt = resolvedAt.toEpochMillis(),
    rewardAmount = reward?.amount,
    rewardCurrency = reward?.currency,
    canonicalUrl = canonicalUrl,
    vulnerabilityInformation = vulnerabilityInformation,
)

internal fun ProgramEntity.toDomain(): Program = Program(
    id = id,
    provider = provider.toProvider(),
    externalId = externalId,
    handle = handle,
    name = name,
)

internal fun Program.toEntity(): ProgramEntity = ProgramEntity(
    id = id,
    provider = provider.name,
    externalId = externalId,
    handle = handle,
    name = name,
)

internal fun ActivityEntity.toDomain(): Activity = Activity(
    id = id,
    provider = provider.toProvider(),
    submissionExternalId = submissionExternalId,
    actor = actor,
    type = type,
    message = message,
    timestamp = timestamp.toInstant(),
)

internal fun Activity.toEntity(): ActivityEntity = ActivityEntity(
    id = id,
    provider = provider.name,
    submissionExternalId = submissionExternalId,
    actor = actor,
    type = type,
    message = message,
    timestamp = timestamp.toEpochMillis(),
)

/* 辅助转换 */

private fun SubmissionEntity.toWeakness(): Weakness? {
    val name = weaknessName ?: return null
    return Weakness(name = name, cweId = weaknessCweId, description = weaknessDescription)
}

private fun SubmissionEntity.toReward(): Reward? {
    val amount = rewardAmount ?: return null
    return Reward(amount = amount, currency = rewardCurrency)
}

private fun String.toProvider(): Provider =
    Provider.entries.firstOrNull { it.name == this } ?: Provider.HACKERONE

private fun String.toStatus(): SubmissionStatus =
    SubmissionStatus.entries.firstOrNull { it.name == this } ?: SubmissionStatus.UNKNOWN

private fun String.toSeverity(): Severity =
    Severity.entries.firstOrNull { it.name == this } ?: Severity.UNKNOWN

private fun Long?.toInstant(): Instant? = this?.let(Instant::ofEpochMilli)

private fun Instant?.toEpochMillis(): Long? = this?.toEpochMilli()
