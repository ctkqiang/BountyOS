package com.bountyos.domain.repository

import com.bountyos.domain.model.Activity
import com.bountyos.domain.model.Program
import com.bountyos.domain.model.Submission
import kotlinx.coroutines.flow.Flow

/**
 * 提交记录的只读仓库契约。
 *
 * UI 通过 [Flow] 订阅本地缓存数据，网络细节对上层完全透明。
 */
interface SubmissionRepository {

    /** 观察所有提交，按最近更新倒序。 */
    fun observeSubmissions(): Flow<List<Submission>>

    /** 观察单个提交详情。 */
    fun observeSubmission(id: String): Flow<Submission?>

    /** 一次性读取单个提交详情。 */
    suspend fun getSubmission(id: String): Submission?

    /** 观察某个提交的活动时间线。 */
    fun observeActivities(submissionExternalId: String): Flow<List<Activity>>

    /** 观察所有活动（统一时间线）。 */
    fun observeAllActivities(): Flow<List<Activity>>

    /** 观察所有项目。 */
    fun observePrograms(): Flow<List<Program>>
}
