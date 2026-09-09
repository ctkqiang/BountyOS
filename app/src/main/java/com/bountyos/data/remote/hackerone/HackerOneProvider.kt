package com.bountyos.data.remote.hackerone

import com.bountyos.data.security.CredentialStore
import com.bountyos.domain.model.Activity
import com.bountyos.domain.model.Program
import com.bountyos.domain.model.Provider
import com.bountyos.domain.model.ProviderPage
import com.bountyos.domain.model.Reward
import com.bountyos.domain.model.Submission
import com.bountyos.domain.provider.BountyProvider

/**
 * HackerOne 平台的只读适配器。
 *
 * 只调用已确认的读取端点（me/reports、reports/{id}）。凭证通过
 * [CredentialStore] 读取，鉴权由网络层的认证拦截器完成，本类不直接
 * 处理 Authorization 头。
 *
 * 尚未在官方文档中确认的端点（activities/programs/rewards）返回空列表，
 * 并在方法注释中标注 UNVERIFIED，绝不臆造请求。
 */
class HackerOneProvider(
    private val api: HackerOneApi,
    private val credentialStore: CredentialStore,
) : BountyProvider {

    override val provider: Provider = Provider.HACKERONE

    override suspend fun validateCredentials(): Result<Unit> = runCatching {
        // 以最小分页请求探测凭证是否有效，401 会以异常形式抛出。
        api.getReports(page = 1, pageSize = 1)
    }

    override suspend fun fetchSubmissions(cursor: String?): ProviderPage<Submission> {
        val response = if (cursor == null) {
            api.getReports(page = 1, pageSize = PAGE_SIZE)
        } else {
            api.getReportsByUrl(cursor)
        }
        return ProviderPage(
            items = response.data.map(HackerOneReport::toSubmission),
            nextCursor = response.links?.next,
        )
    }

    override suspend fun fetchSubmission(externalId: String): Submission =
        api.getReport(externalId).data.toSubmission()

    /*
     * UNVERIFIED:
     * HackerOne 官方 hacker-resources 文档未在本实现中确认 activities
     * 的独立端点。activity 是 report 的关系资源，可能通过
     * `GET /hackers/reports/{id}/activities` 或 `?include=activities`
     * 获取。在确认前返回空，避免臆造。
     */
    override suspend fun fetchActivities(externalId: String): List<Activity> = emptyList()

    /*
     * UNVERIFIED:
     * programs 端点（推测为 `GET /hackers/me/programs`）未确认，返回空。
     */
    override suspend fun fetchPrograms(): List<Program> = emptyList()

    /*
     * UNVERIFIED:
     * 独立奖励（earning）端点未确认，返回空。奖励信息可从报告关系中的
     * bounties 聚合得到，已在 fetchSubmission 中处理。
     */
    override suspend fun fetchRewards(): List<Reward> = emptyList()

    private companion object {
        const val PAGE_SIZE = 100
    }
}
