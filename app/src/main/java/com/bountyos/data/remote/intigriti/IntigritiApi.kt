package com.bountyos.data.remote.intigriti

import retrofit2.http.GET

/**
 * Intigriti 外部 API 的 Retrofit 服务定义。
 *
 * 仅声明读取端点。submissions 端点在本实现中尚未确认，故未声明，
 * 待官方文档确认后补充。
 */
interface IntigritiApi {

    /** 连接验证与项目列表，`GET /v2/programs`。 */
    @GET("v2/programs")
    suspend fun getPrograms(): List<IntigritiProgram>
}
