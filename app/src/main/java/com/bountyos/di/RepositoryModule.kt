package com.bountyos.di

import com.bountyos.data.repository.DefaultSyncCoordinator
import com.bountyos.data.repository.IntegrationRepositoryImpl
import com.bountyos.data.repository.SubmissionRepositoryImpl
import com.bountyos.domain.repository.IntegrationRepository
import com.bountyos.domain.repository.SubmissionRepository
import com.bountyos.domain.repository.SyncCoordinator
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * 数据层接口与其实现的 Hilt 绑定。
 *
 * domain 层只依赖接口，具体实现通过 @Binds 注入，保持依赖倒置。
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindSubmissionRepository(impl: SubmissionRepositoryImpl): SubmissionRepository

    @Binds
    @Singleton
    abstract fun bindIntegrationRepository(impl: IntegrationRepositoryImpl): IntegrationRepository

    @Binds
    @Singleton
    abstract fun bindSyncCoordinator(impl: DefaultSyncCoordinator): SyncCoordinator
}
