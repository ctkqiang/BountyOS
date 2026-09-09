package com.bountyos.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bountyos.data.local.entity.SubmissionEntity
import kotlinx.coroutines.flow.Flow

/**
 * 提交记录的本地数据访问接口。
 *
 * UI 通过 Room 支持的 [Flow] 订阅数据变化，实现「本地作为 UI 唯一
 * 数据源」的架构约定。
 */
@Dao
interface SubmissionDao {

    /** 观察所有提交，按最近更新倒序。 */
    @Query("SELECT * FROM submissions ORDER BY updated_at DESC")
    fun observeAll(): Flow<List<SubmissionEntity>>

    /** 观察单个提交详情。 */
    @Query("SELECT * FROM submissions WHERE id = :id")
    fun observeById(id: String): Flow<SubmissionEntity?>

    /** 一次性读取单个提交详情。 */
    @Query("SELECT * FROM submissions WHERE id = :id")
    suspend fun getById(id: String): SubmissionEntity?

    /** 批量写入（重复键覆盖）。 */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(submissions: List<SubmissionEntity>)
}
