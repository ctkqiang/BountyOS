package com.bountyos.data.remote.bugcrowd

import com.bountyos.domain.model.Provider
import com.bountyos.domain.model.Submission
import com.bountyos.domain.normalization.StatusNormalizer

/*
 * Bugcrowd DTO -> 领域模型的映射。
 *
 * Bugcrowd 的 severity 使用 P1-P5 优先级，官方未提供与
 * low/medium/high/critical 的一一对应，因此 severity 归一化为 null，
 * 保留原始优先级字符串。时间、奖励、项目等未确认字段保持为 null。
 */

internal fun BugcrowdSubmission.toSubmission(): Submission {
    val attributes = attributes
    return Submission(
        id = submissionId(Provider.BUGCROWD, id),
        provider = Provider.BUGCROWD,
        externalId = id,
        programName = null,
        title = attributes?.title.orEmpty(),
        status = StatusNormalizer.fromBugcrowd(attributes?.state),
        providerStatus = attributes?.state.orEmpty(),
        severity = null,
        providerSeverity = attributes?.severity,
        weakness = null,
        submittedAt = null,
        updatedAt = null,
        resolvedAt = null,
        reward = null,
        canonicalUrl = null,
        vulnerabilityInformation = attributes?.description,
    )
}

internal fun submissionId(provider: Provider, externalId: String): String =
    "${provider.name}:$externalId"
