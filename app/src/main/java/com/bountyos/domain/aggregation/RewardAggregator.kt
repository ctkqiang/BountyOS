package com.bountyos.domain.aggregation

import com.bountyos.domain.model.Submission
import java.math.BigDecimal

/**
 * 奖励聚合器。
 *
 * 按货币分组求和。不同货币之间绝不隐式换算——BountyOS 不维护汇率。
 * 金额使用 [BigDecimal] 精确累加，输出保留原始精度。
 */
object RewardAggregator {

    const val UNKNOWN_CURRENCY = "UNKNOWN"

    fun aggregate(submissions: List<Submission>): List<RewardTotal> =
        submissions
            .mapNotNull { it.reward }
            .groupBy { it.currency ?: UNKNOWN_CURRENCY }
            .map { (currency, rewards) ->
                val total = rewards
                    .mapNotNull { reward -> reward.amount.toBigDecimalOrNull() }
                    .fold(BigDecimal.ZERO, BigDecimal::add)
                RewardTotal(currency = currency, amount = total.toPlainString())
            }
            .sortedBy { it.currency }
}
