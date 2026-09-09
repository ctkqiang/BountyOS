package com.bountyos.domain.aggregation

import com.bountyos.domain.model.Provider
import com.bountyos.domain.model.Submission
import com.bountyos.domain.normalization.AttentionCalculator

/**
 * Dashboard 的统计结果。
 */
data class DashboardStats(
    val totalRewards: List<RewardTotal>,
    val submissionCount: Int,
    val hackerOneCount: Int,
    val bugcrowdCount: Int,
    val attentionCount: Int,
    val recentSubmissions: List<Submission>,
)

/**
 * Dashboard 统计计算器（纯函数）。
 *
 * 所有统计均从本地缓存的提交数据推导，不产生任何网络请求或副作用，
 * 便于单元测试。
 */
object DashboardStatsCalculator {

    fun calculate(submissions: List<Submission>): DashboardStats = DashboardStats(
        totalRewards = RewardAggregator.aggregate(submissions),
        submissionCount = submissions.size,
        hackerOneCount = submissions.count { it.provider == Provider.HACKERONE },
        bugcrowdCount = submissions.count { it.provider == Provider.BUGCROWD },
        attentionCount = submissions.count { AttentionCalculator.requiresAttention(it.status) },
        recentSubmissions = submissions.take(RECENT_COUNT),
    )

    private const val RECENT_COUNT = 5
}
