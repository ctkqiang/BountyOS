package com.bountyos.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bountyos.data.local.entity.SyncStateEntity

/**
 * 同步游标状态的本地访问接口。
 */
@Dao
interface SyncStateDao {

    @Query("SELECT * FROM sync_state WHERE provider = :provider")
    suspend fun get(provider: String): SyncStateEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(state: SyncStateEntity)
}
