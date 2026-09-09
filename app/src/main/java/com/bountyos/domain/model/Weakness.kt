package com.bountyos.domain.model

/**
 * 漏洞类型（Weakness / CWE）。
 *
 * 来自平台返回的 weakness 资源，通常对应 CWE（Common Weakness
 * Enumeration）分类。不同平台的字段结构不同，这里只保留对用户
 * 有展示意义的子集。
 */
data class Weakness(
    /** 弱点名称，例如 "Cross-Site Scripting"。 */
    val name: String,
    /** CWE 编号，例如 "79"。可能为空。 */
    val cweId: String?,
    /** 弱点描述。可能为空。 */
    val description: String?,
)
