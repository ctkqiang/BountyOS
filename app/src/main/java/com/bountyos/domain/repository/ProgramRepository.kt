package com.bountyos.domain.repository

import com.bountyos.domain.model.Program
import kotlinx.coroutines.flow.Flow

/**
 * 项目（Program）的只读仓库。
 *
 * 只暴露读取操作，符合 BountyOS 的只读原则。项目数据来自各平台的
 * 项目列表同步，例如 Intigriti 的关注/订阅项目。
 */
interface ProgramRepository {

    /** 观察所有已同步的项目，按名称排序。 */
    fun observePrograms(): Flow<List<Program>>
}
