package com.bountyos.domain.aggregation

/**
 * 按货币聚合后的奖励总额。
 *
 * 金额以字符串表示，避免浮点精度问题。货币以 ISO 4217 代码表示，
 * 未知货币以 [RewardAggregator.UNKNOWN_CURRENCY] 标记，绝不隐式换算。
 */
data class RewardTotal(
    val currency: String,
    val amount: String,
)
