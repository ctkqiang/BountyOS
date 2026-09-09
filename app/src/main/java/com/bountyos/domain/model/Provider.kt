package com.bountyos.domain.model

/**
 * 已支持的 bug bounty 平台。
 *
 * 该枚举是 provider-neutral 的领域标识，用于在整个应用中
 * 区分不同平台的数据，避免依赖各平台私有的 DTO 类型。
 */
enum class Provider {
    HACKERONE,
    BUGCROWD,
    INTIGRITI,
    YESWEHACK,
}
