package com.bountyos.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bountyos.data.local.entity.ProgramEntity
import kotlinx.coroutines.flow.Flow

/**
 * 项目的本地访问接口。
 */
@Dao
interface ProgramDao {

    @Query("SELECT * FROM programs ORDER BY name ASC")
    fun observeAll(): Flow<List<ProgramEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(programs: List<ProgramEntity>)
}
