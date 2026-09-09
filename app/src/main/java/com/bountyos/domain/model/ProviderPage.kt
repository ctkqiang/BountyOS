package com.bountyos.domain.model

/**
 * 分页结果容器。
 *
 * provider 的分页机制可能不同（HackerOne 返回 `links.next` URL，
 * Bugcrowd 使用页码），因此统一抽象为不透明的 [nextCursor]。
 * 适配器负责把平台自身的分页标记转换为该游标。
 */
data class ProviderPage<T>(
    /** 当前页数据。 */
    val items: List<T>,
    /** 下一页游标；为空表示没有更多数据。 */
    val nextCursor: String?,
)
