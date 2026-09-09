package com.bountyos.data.remote.bugcrowd

import com.bountyos.data.security.CredentialStore
import com.bountyos.domain.model.Activity
import com.bountyos.domain.model.Program
import com.bountyos.domain.model.Provider
import com.bountyos.domain.model.ProviderPage
import com.bountyos.domain.model.Reward
import com.bountyos.domain.model.Submission
import com.bountyos.domain.provider.BountyProvider

/**
 * Bugcrowd 平台的只读适配器。
 *
 * 只调用已确认的读取端点（`/submissions`、`/submissions/{id}`）。
 * 鉴权由网络层的 `Token` 拦截器完成。
 *
 * activities/programs/rewards 的端点未确认，返回空并标注 UNVERIFIED。
 */
class BugcrowdProvider(
    private val api: BugcrowdApi,
    private val credentialStore: CredentialStore,
) : BountyProvider {

    override val provider: Provider = Provider.BUGCROWD

    override suspend fun validateCredentials(): Result<Unit> = runCatching {
        // 以一次提交列表请求探测凭证是否有效。
        api.getSubmissions()
    }

    override suspend fun fetchSubmissions(cursor: String?): ProviderPage<Submission> {
        val response = api.getSubmissions()
        return ProviderPage(
            items = response.data.map(BugcrowdSubmission::toSubmission),
            // UNVERIFIED: Bugcrowd 列表分页游标结构未确认，暂不实现增量翻页。
            nextCursor = null,
        )
    }

    override suspend fun fetchSubmission(externalId: String): Submission =
        api.getSubmission(externalId).data.toSubmission()

    override suspend fun fetchActivities(externalId: String): List<Activity> = emptyList()

    override suspend fun fetchPrograms(): List<Program> = emptyList()

    override suspend fun fetchRewards(): List<Reward> = emptyList()
}
