package com.bountyos.data.remote.yeswehack

import kotlinx.serialization.Serializable

/**
 * YesWeHack 报告（Report）DTO。
 *
 * 字段对应官方确认的 `GET /reports/{id}` 响应（id、local_id、title、
 * scope、currency）。status 字段名沿用惯例，标注 UNVERIFIED。
 */
@Serializable
data class YesWeHackReport(
    val id: Long? = null,
    val local_id: String? = null,
    val title: String? = null,
    val scope: String? = null,
    val currency: String? = null,
    val status: String? = null,
)
