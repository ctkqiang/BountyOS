package com.bountyos.data.repository

import com.bountyos.data.local.dao.ProgramDao
import com.bountyos.data.local.mapper.toDomain
import com.bountyos.domain.model.Program
import com.bountyos.domain.repository.ProgramRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 项目仓库的默认实现。
 *
 * 直接读取 Room 中的 programs 表并映射为领域模型。
 */
@Singleton
class ProgramRepositoryImpl @Inject constructor(
    private val programDao: ProgramDao,
) : ProgramRepository {

    override fun observePrograms(): Flow<List<Program>> =
        programDao.observeAll().map { entities -> entities.map { it.toDomain() } }
}
