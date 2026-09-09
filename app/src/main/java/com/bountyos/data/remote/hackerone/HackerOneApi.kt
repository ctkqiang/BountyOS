package com.bountyos.data.remote.hackerone

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

/**
 * HackerOne Hacker API 的 Retrofit 服务定义。
 *
 * 仅声明读取端点，不声明任何写入端点（提交、评论、改状态等）。
 *
 * HackerOne 官方文档：https://api.hackerone.com/hacker-resources/
 */
interface HackerOneApi {

    /**
     * 拉取当前 hacker 的报告列表（分页）。
     *
     * 官方端点：`GET /hackers/me/reports`。
     */
    @GET("hackers/me/reports")
    suspend fun getReports(
        @Query("page[number]") page: Int? = null,
        @Query("page[size]") pageSize: Int? = null,
    ): HackerOneReportsResponse

    /**
     * 通过 `links.next` 提供的完整 URL 拉取下一页。
     *
     * HackerOne 分页返回绝对 URL，因此使用 [Url] 覆盖 base URL。
     */
    @GET
    suspend fun getReportsByUrl(@Url url: String): HackerOneReportsResponse

    /**
     * 拉取单个报告详情。
     *
     * 官方端点：`GET /hackers/reports/{id}`。
     */
    @GET("hackers/reports/{id}")
    suspend fun getReport(@Path("id") id: String): HackerOneReportResponse
}
