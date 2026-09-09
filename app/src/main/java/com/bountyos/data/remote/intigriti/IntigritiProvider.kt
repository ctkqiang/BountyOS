package com.bountyos.data.remote.intigriti

import com.bountyos.domain.model.Activity
import com.bountyos.domain.model.Program
import com.bountyos.domain.model.Provider
import com.bountyos.domain.model.ProviderPage
import com.bountyos.domain.model.Reward
import com.bountyos.domain.model.Submission
import com.bountyos.domain.provider.BountyProvider

/**
 * Intigriti 平台的只读适配器。
 *
 * 已确认端点：`GET /v2/programs`（连接验证 + 项目列表）。
 * submissions / activities / rewards 的端点未确认，返回空并标注 UNVERIFIED。
 */
class IntigritiProvider(
    private val api: IntigritiApi,
) : BountyProvider {

    override val provider: Provider = Provider.INTIGRITI

    override suspend fun validateCredentials(): Result<Unit> = runCatching {
        api.getPrograms()
    }

    // UNVERIFIED: Intigriti submissions 列表端点与分页结构未确认，暂不拉取。
    override suspend fun fetchSubmissions(cursor: String?): ProviderPage<Submission> =
        ProviderPage(items = emptyList(), nextCursor = null)

    // UNVERIFIED: Intigriti submission 详情端点未确认。
    override suspend fun fetchSubmission(externalId: String): Submission =
        throw UnsupportedOperationException("Intigriti submission detail endpoint UNVERIFIED")

    override suspend fun fetchActivities(externalId: String): List<Activity> = emptyList()

    override suspend fun fetchPrograms(): List<Program> =
        api.getPrograms().map { program ->
            Program(
                id = "intigriti-${program.id}",
                provider = Provider.INTIGRITI,
                externalId = program.id,
                handle = program.handle ?: program.name,
                name = program.name,
            )
        }

    override suspend fun fetchRewards(): List<Reward> = emptyList()
}
