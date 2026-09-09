package com.bountyos.domain.normalization

import com.bountyos.domain.model.Severity
import com.bountyos.domain.model.SubmissionStatus

/**
 * 平台状态与严重程度到 BountyOS 规范模型的映射。
 *
 * 所有映射均以平台官方文档为依据：
 *  - HackerOne report-states 枚举（hacker-reference）
 *  - Bugcrowd submission state 枚举（docs.bugcrowd.com filtering/submission-status）
 *
 * 未被文档明确定义的值一律映射到 [SubmissionStatus.UNKNOWN] /
 * [Severity.UNKNOWN]，并在上层保留原始值，绝不猜测。
 */
object StatusNormalizer {

    /**
     * 将 HackerOne 的 report `state` 映射为规范状态。
     *
     * HackerOne 官方枚举值：
     * new, pending-program-review, triaged, needs-more-info, retesting,
     * resolved, not-applicable, informative, duplicate, spam。
     */
    fun fromHackerOne(state: String?): SubmissionStatus = when (state) {
        "new" -> SubmissionStatus.OPEN
        "pending-program-review" -> SubmissionStatus.TRIAGED
        "triaged" -> SubmissionStatus.TRIAGED
        "needs-more-info" -> SubmissionStatus.ACTION_REQUIRED
        "retesting" -> SubmissionStatus.RETESTING
        "resolved" -> SubmissionStatus.RESOLVED
        "not-applicable" -> SubmissionStatus.REJECTED
        "informative" -> SubmissionStatus.INFORMATIVE
        "duplicate" -> SubmissionStatus.DUPLICATE
        "spam" -> SubmissionStatus.REJECTED
        else -> SubmissionStatus.UNKNOWN
    }

    /**
     * 将 Bugcrowd 的 submission `state` 映射为规范状态。
     *
     * Bugcrowd 官方枚举值：
     * new, triaged, unresolved, resolved, informational,
     * out-of-scope, not-reproducible, not-applicable。
     */
    fun fromBugcrowd(state: String?): SubmissionStatus = when (state) {
        "new" -> SubmissionStatus.OPEN
        "triaged" -> SubmissionStatus.TRIAGED
        // "unresolved" 是「已接受、待修复」的有效问题，归入需要继续关注的已确认状态。
        "unresolved" -> SubmissionStatus.TRIAGED
        "resolved" -> SubmissionStatus.RESOLVED
        "informational" -> SubmissionStatus.INFORMATIVE
        "out-of-scope" -> SubmissionStatus.REJECTED
        "not-reproducible" -> SubmissionStatus.REJECTED
        "not-applicable" -> SubmissionStatus.REJECTED
        else -> SubmissionStatus.UNKNOWN
    }

    /**
     * 将 HackerOne 的 `severity_rating` 映射为规范严重程度。
     *
     * 官方枚举值：none, low, medium, high, critical。
     */
    fun hackerOneSeverity(rating: String?): Severity? = when (rating) {
        "none" -> Severity.NONE
        "low" -> Severity.LOW
        "medium" -> Severity.MEDIUM
        "high" -> Severity.HIGH
        "critical" -> Severity.CRITICAL
        else -> null
    }
}
