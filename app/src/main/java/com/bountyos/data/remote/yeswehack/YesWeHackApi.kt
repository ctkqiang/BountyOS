package com.bountyos.data.remote.yeswehack

import retrofit2.http.GET
import retrofit2.http.Path

/**
 * YesWeHack API 的 Retrofit 服务定义。
 *
 * 仅声明读取端点。`GET /reports/{id}` 为官方确认端点；`GET /reports`
 * 列表端点尚未完全确认，标注 UNVERIFIED。
 */
interface YesWeHackApi {

    /** 报告列表，`GET /reports`（UNVERIFIED）。 */
    @GET("reports")
    suspend fun getReports(): List<YesWeHackReport>

    /** 报告详情，`GET /reports/{id}`（官方确认）。 */
    @GET("reports/{id}")
    suspend fun getReport(@Path("id") id: String): YesWeHackReport
}
