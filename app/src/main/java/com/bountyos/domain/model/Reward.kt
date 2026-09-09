package com.bountyos.domain.model

/**
 * 奖励金额。
 *
 * 金额使用 [String] 而非浮点类型存储，以避免二进制浮点导致的精度
 * 丢失（例如 `0.1 + 0.2`）。货币以 ISO 4217 代码表示，例如 `USD`。
 *
 * 不同货币之间不做隐式换算，因为 BountyOS 不维护任何汇率信息。
 */
data class Reward(
    /** 金额的原始字符串表示，例如 "1000.00"。 */
    val amount: String,
    /** ISO 4217 货币代码，例如 "USD"。可能为空表示未知货币。 */
    val currency: String?,
)
