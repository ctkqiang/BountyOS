package com.bountyos.data.remote.intigriti

import kotlinx.serialization.Serializable

/**
 * Intigriti 项目（Program）DTO。
 *
 * 对应 `GET /v2/programs` 返回数组中的元素。官方文档：
 * https://intigriti.readme.io/reference/introduction
 */
@Serializable
data class IntigritiProgram(
    val id: String,
    val name: String,
    val handle: String? = null,
    val companyHandle: String? = null,
)
