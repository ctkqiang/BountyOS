package com.bountyos.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bountyos.data.local.entity.ActivityEntity
import kotlinx.coroutines.flow.Flow

/**
 * 报告活动的本地访问接口。
 */
@Dao
interface ActivityDao {

    @Query(
        "SELECT * FROM activities WHERE submission_external_id = :submissionId " +
            "ORDER BY timestamp DESC"
    )
    fun observeBySubmission(submissionId: String): Flow<List<ActivityEntity>>

    /** 观察所有活动，按时间倒序（用于统一时间线）。 */
    @Query("SELECT * FROM activities ORDER BY timestamp DESC")
    fun observeAll(): Flow<List<ActivityEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(activities: List<ActivityEntity>)
}
