package com.bountyos.data.repository

import com.bountyos.data.local.dao.ActivityDao
import com.bountyos.data.local.dao.ProgramDao
import com.bountyos.data.local.dao.SubmissionDao
import com.bountyos.data.local.mapper.toDomain
import com.bountyos.domain.model.Activity
import com.bountyos.domain.model.Program
import com.bountyos.domain.model.Submission
import com.bountyos.domain.repository.SubmissionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 提交仓库的默认实现。
 *
 * 完全面向本地 Room 缓存，UI 观察到的数据来自数据库 Flow，而非
 * 直接访问网络。网络同步由 [SyncCoordinator] 负责。
 */
@Singleton
class SubmissionRepositoryImpl @Inject constructor(
    private val submissionDao: SubmissionDao,
    private val programDao: ProgramDao,
    private val activityDao: ActivityDao,
) : SubmissionRepository {

    override fun observeSubmissions(): Flow<List<Submission>> =
        submissionDao.observeAll().map { entities -> entities.map { it.toDomain() } }

    override fun observeSubmission(id: String): Flow<Submission?> =
        submissionDao.observeById(id).map { it?.toDomain() }

    override suspend fun getSubmission(id: String): Submission? =
        submissionDao.getById(id)?.toDomain()

    override fun observeActivities(submissionExternalId: String): Flow<List<Activity>> =
        activityDao.observeBySubmission(submissionExternalId)
            .map { entities -> entities.map { it.toDomain() } }

    override fun observeAllActivities(): Flow<List<Activity>> =
        activityDao.observeAll().map { entities -> entities.map { it.toDomain() } }

    override fun observePrograms(): Flow<List<Program>> =
        programDao.observeAll().map { entities -> entities.map { it.toDomain() } }
}
