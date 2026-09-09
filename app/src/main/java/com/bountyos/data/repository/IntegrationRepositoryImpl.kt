package com.bountyos.data.repository

import com.bountyos.data.local.dao.IntegrationDao
import com.bountyos.data.local.entity.IntegrationEntity
import com.bountyos.data.security.Credential
import com.bountyos.data.security.CredentialStore
import com.bountyos.di.Bugcrowd
import com.bountyos.di.HackerOne
import com.bountyos.domain.model.Integration
import com.bountyos.domain.model.Provider
import com.bountyos.domain.provider.BountyProvider
import com.bountyos.domain.repository.IntegrationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 平台集成的默认实现。
 *
 * 连接时先保存凭证并校验；校验失败会立即清除凭证，避免残留无效凭据。
 */
@Singleton
class IntegrationRepositoryImpl @Inject constructor(
    private val integrationDao: IntegrationDao,
    private val credentialStore: CredentialStore,
    @HackerOne private val hackerOneProvider: BountyProvider,
    @Bugcrowd private val bugcrowdProvider: BountyProvider,
) : IntegrationRepository {

    override fun observeIntegrations(): Flow<List<Integration>> =
        integrationDao.observeAll().map { entities -> entities.map { it.toDomain() } }

    override suspend fun connect(
        provider: Provider,
        username: String?,
        token: String,
    ): Result<Unit> {
        credentialStore.save(provider, Credential(username = username, token = token))
        return providerOf(provider).validateCredentials().fold(
            onSuccess = {
                integrationDao.upsert(
                    IntegrationEntity(
                        provider = provider.name,
                        connected = true,
                        lastSyncedAt = System.currentTimeMillis(),
                    )
                )
                Result.success(Unit)
            },
            onFailure = { throwable ->
                credentialStore.clear(provider)
                Result.failure(throwable)
            },
        )
    }

    override suspend fun disconnect(provider: Provider) {
        credentialStore.clear(provider)
        integrationDao.delete(provider.name)
    }

    private fun providerOf(provider: Provider): BountyProvider = when (provider) {
        Provider.HACKERONE -> hackerOneProvider
        Provider.BUGCROWD -> bugcrowdProvider
    }

    private fun IntegrationEntity.toDomain(): Integration = Integration(
        provider = provider.toProvider(),
        connected = connected,
        lastSyncedAt = lastSyncedAt?.let(Instant::ofEpochMilli),
    )

    private fun String.toProvider(): Provider =
        Provider.entries.firstOrNull { it.name == this } ?: Provider.HACKERONE
}
