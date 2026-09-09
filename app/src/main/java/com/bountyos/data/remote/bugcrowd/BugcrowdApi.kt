package com.bountyos.data.remote.bugcrowd

import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Bugcrowd API 的 Retrofit 服务定义。
 *
 * 仅声明读取端点。官方文档：https://docs.bugcrowd.com/api/
 */
interface BugcrowdApi {

    /**
     * 拉取提交列表。
     *
     * 端点 `GET /submissions` 与列表分页参数尚未在本实现中完全确认，
     * 当前拉取默认第一页。
     */
    @GET("submissions")
    suspend fun getSubmissions(): BugcrowdSubmissionsResponse

    /**
     * 拉取单个提交详情。
     *
     * 官方端点：`GET /submissions/{id}`。
     */
    @GET("submissions/{id}")
    suspend fun getSubmission(@Path("id") id: String): BugcrowdSubmissionResponse
}
