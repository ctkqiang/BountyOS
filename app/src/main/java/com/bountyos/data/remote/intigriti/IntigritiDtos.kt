package com.bountyos.data.remote.intigriti

import kotlinx.serialization.Serializable

/**
 * Intigriti Researcher API 项目（Program）DTO。
 *
 * 对应 `GET /programs` 返回的 `records` 数组元素。
 * 官方文档：https://api.intigriti.com/external/researcher/v1
 */
@Serializable
data class IntigritiProgram(
    val id: String,
    val name: String,
    val handle: String? = null,
)

/**
 * Researcher API `GET /programs` 的分页响应。
 *
 * 结构为 `{ "records": [...], "maxCount": N }`，采用 offset 分页。
 */
@Serializable
data class IntigritiProgramPage(
    val records: List<IntigritiProgram> = emptyList(),
    val maxCount: Int = 0,
)
