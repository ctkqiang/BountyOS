package com.bountyos.data.repository

import com.bountyos.data.local.dao.IntegrationDao
import com.bountyos.data.local.dao.ProgramDao
import com.bountyos.data.local.dao.SubmissionDao
import com.bountyos.data.local.dao.SyncStateDao
import com.bountyos.data.local.entity.IntegrationEntity
import com.bountyos.data.local.entity.SyncStateEntity
import com.bountyos.data.local.mapper.toEntity
import com.bountyos.di.Bugcrowd
import com.bountyos.di.HackerOne
import com.bountyos.di.Intigriti
import com.bountyos.di.YesWeHack
import com.bountyos.domain.model.Program
import com.bountyos.domain.model.Provider
import com.bountyos.domain.model.Submission
import com.bountyos.domain.provider.BountyProvider
import com.bountyos.domain.repository.SyncCoordinator
import com.bountyos.domain.repository.SyncResult
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 同步协调器的默认实现。
 *
 * 负责从已连接平台拉取全部提交并写入本地缓存，同时更新同步游标与
 * 集成状态。同步是只读的，绝不向平台写入任何数据。
 */
@Singleton
class DefaultSyncCoordinator @Inject constructor(
    private val submissionDao: SubmissionDao,
    private val programDao: ProgramDao,
    private val integrationDao: IntegrationDao,
    private val syncStateDao: SyncStateDao,
    @HackerOne private val hackerOneProvider: BountyProvider,
    @Bugcrowd private val bugcrowdProvider: BountyProvider,
    @Intigriti private val intigritiProvider: BountyProvider,
    @YesWeHack private val yeswehackProvider: BountyProvider,
) : SyncCoordinator {

    override suspend fun synchronize(): SyncResult {
        val failures = mutableMapOf<Provider, Throwable>()
        connectedProviders().forEach { provider ->
            val result = synchronize(provider)
            if (result is SyncResult.Failed) failures.putAll(result.failures)
        }
        return if (failures.isEmpty()) SyncResult.Success else SyncResult.Failed(failures)
    }

    override suspend fun synchronize(provider: Provider): SyncResult = runCatching {
        val providerImpl = providerOf(provider)
        val submissions = fetchAllSubmissions(providerImpl)
        val programs = providerImpl.fetchPrograms()
        val now = System.currentTimeMillis()
        submissionDao.upsertAll(submissions.map(Submission::toEntity))
        programDao.upsertAll(programs.map(Program::toEntity))
        syncStateDao.upsert(SyncStateEntity(provider = provider.name, cursor = null, lastSyncedAt = now))
        integrationDao.upsert(IntegrationEntity(provider = provider.name, connected = true, lastSyncedAt = now))
    }.fold(
        onSuccess = { SyncResult.Success },
        onFailure = { throwable -> SyncResult.Failed(mapOf(provider to throwable)) },
    )

    private suspend fun fetchAllSubmissions(provider: BountyProvider): List<Submission> {
        val all = mutableListOf<Submission>()
        var cursor: String? = null
        do {
            val page = provider.fetchSubmissions(cursor)
            all += page.items
            cursor = page.nextCursor
        } while (cursor != null)
        return all
    }

    private suspend fun connectedProviders(): List<Provider> =
        integrationDao.observeAll().first()
            .filter { it.connected }
            .map { it.provider.toProvider() }

    private fun providerOf(provider: Provider): BountyProvider = when (provider) {
        Provider.HACKERONE -> hackerOneProvider
        Provider.BUGCROWD -> bugcrowdProvider
        Provider.INTIGRITI -> intigritiProvider
        Provider.YESWEHACK -> yeswehackProvider
    }

    private fun String.toProvider(): Provider =
        Provider.entries.firstOrNull { it.name == this } ?: Provider.HACKERONE
}
