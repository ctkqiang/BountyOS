package com.bountyos.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bountyos.data.local.entity.IntegrationEntity
import kotlinx.coroutines.flow.Flow

/**
 * 平台集成状态的本地访问接口。
 */
@Dao
interface IntegrationDao {

    @Query("SELECT * FROM integrations")
    fun observeAll(): Flow<List<IntegrationEntity>>

    @Query("SELECT * FROM integrations WHERE provider = :provider")
    suspend fun get(provider: String): IntegrationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(integration: IntegrationEntity)

    @Query("DELETE FROM integrations WHERE provider = :provider")
    suspend fun delete(provider: String)
}
