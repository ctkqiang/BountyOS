package com.bountyos.data.remote.yeswehack

import com.bountyos.domain.model.Activity
import com.bountyos.domain.model.Program
import com.bountyos.domain.model.Provider
import com.bountyos.domain.model.ProviderPage
import com.bountyos.domain.model.Reward
import com.bountyos.domain.model.Submission
import com.bountyos.domain.model.SubmissionStatus
import com.bountyos.domain.provider.BountyProvider

/**
 * YesWeHack 平台的只读适配器。
 *
 * 已确认端点：`GET /reports/{id}`。列表端点 `GET /reports` 与部分
 * 字段（status、reward、description）标注 UNVERIFIED。activities /
 * programs / rewards 端点未确认，返回空。
 */
class YesWeHackProvider(
    private val api: YesWeHackApi,
) : BountyProvider {

    override val provider: Provider = Provider.YESWEHACK

    override suspend fun validateCredentials(): Result<Unit> = runCatching {
        api.getReports()
    }

    // UNVERIFIED: 列表端点未完全确认，按返回数组解析。
    override suspend fun fetchSubmissions(cursor: String?): ProviderPage<Submission> =
        ProviderPage(
            items = api.getReports().map(YesWeHackReport::toSubmission),
            nextCursor = null,
        )

    override suspend fun fetchSubmission(externalId: String): Submission =
        api.getReport(externalId).toSubmission()

    override suspend fun fetchActivities(externalId: String): List<Activity> = emptyList()

    override suspend fun fetchPrograms(): List<Program> = emptyList()

    override suspend fun fetchRewards(): List<Reward> = emptyList()
}

private fun YesWeHackReport.toSubmission(): Submission = Submission(
    id = "yeswehack-${id}",
    provider = Provider.YESWEHACK,
    externalId = id?.toString() ?: local_id.orEmpty(),
    programName = null,
    title = title.orEmpty(),
    status = toSubmissionStatus(status),
    providerStatus = status.orEmpty(),
    severity = null,
    providerSeverity = null,
    weakness = null,
    submittedAt = null,
    updatedAt = null,
    resolvedAt = null,
    reward = null,
    canonicalUrl = id?.let { "https://yeswehack.com/reports/$it" },
    vulnerabilityInformation = null,
)

private fun toSubmissionStatus(status: String?): SubmissionStatus = when (status?.lowercase()) {
    "new" -> SubmissionStatus.OPEN
    "under review", "under_review" -> SubmissionStatus.TRIAGED
    "need more info", "need_more_info" -> SubmissionStatus.ACTION_REQUIRED
    "accepted" -> SubmissionStatus.TRIAGED
    "resolved" -> SubmissionStatus.RESOLVED
    "duplicate" -> SubmissionStatus.DUPLICATE
    "not applicable", "invalid", "out of scope", "out_of_scope", "spam", "rtfs" -> SubmissionStatus.REJECTED
    "wont_fix", "won't fix", "informative" -> SubmissionStatus.INFORMATIVE
    else -> SubmissionStatus.UNKNOWN
}
