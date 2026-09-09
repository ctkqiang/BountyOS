package com.bountyos.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.bountyos.data.local.dao.ActivityDao
import com.bountyos.data.local.dao.IntegrationDao
import com.bountyos.data.local.dao.ProgramDao
import com.bountyos.data.local.dao.SubmissionDao
import com.bountyos.data.local.dao.SyncStateDao
import com.bountyos.data.local.entity.ActivityEntity
import com.bountyos.data.local.entity.IntegrationEntity
import com.bountyos.data.local.entity.ProgramEntity
import com.bountyos.data.local.entity.SubmissionEntity
import com.bountyos.data.local.entity.SyncStateEntity

/**
 * BountyOS 的 Room 数据库。
 *
 * 数据库只存储缓存与连接元数据，凭证由 Keystore 安全存储，绝不进入
 * Room。数据库是 UI 的本地数据源，远程数据同步完成后写入这里。
 */
@Database(
    entities = [
        SubmissionEntity::class,
        ProgramEntity::class,
        ActivityEntity::class,
        IntegrationEntity::class,
        SyncStateEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
abstract class BountyOsDatabase : RoomDatabase() {

    abstract fun submissionDao(): SubmissionDao

    abstract fun programDao(): ProgramDao

    abstract fun activityDao(): ActivityDao

    abstract fun integrationDao(): IntegrationDao

    abstract fun syncStateDao(): SyncStateDao
}
