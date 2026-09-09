package com.bountyos.domain.model

/**
 * 平台中立的项目（Program）。
 *
 * 项目是研究者提交漏洞的对象。不同平台对项目的字段结构不同，
 * 这里保留对展示有用的最小子集。
 */
data class Program(
    /** 本地唯一标识。 */
    val id: String,
    /** 来源平台。 */
    val provider: Provider,
    /** 平台内部项目 ID，可能为空。 */
    val externalId: String?,
    /** 项目句柄，例如 HackerOne 的 "gitlab"。 */
    val handle: String,
    /** 项目展示名称。 */
    val name: String?,
)
