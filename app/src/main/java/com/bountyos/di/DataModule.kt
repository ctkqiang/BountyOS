package com.bountyos.di

import android.content.Context
import android.content.pm.ApplicationInfo
import androidx.room.Room
import com.bountyos.data.local.BountyOsDatabase
import com.bountyos.data.local.dao.ActivityDao
import com.bountyos.data.local.dao.IntegrationDao
import com.bountyos.data.local.dao.ProgramDao
import com.bountyos.data.local.dao.SubmissionDao
import com.bountyos.data.local.dao.SyncStateDao
import com.bountyos.data.remote.ApiJson
import com.bountyos.data.remote.OpenAiClient
import com.bountyos.data.remote.bugcrowd.BugcrowdApi
import com.bountyos.data.remote.bugcrowd.BugcrowdAuthInterceptor
import com.bountyos.data.remote.bugcrowd.BugcrowdContentTypeInterceptor
import com.bountyos.data.remote.bugcrowd.BugcrowdProvider
import com.bountyos.data.remote.hackerone.HackerOneApi
import com.bountyos.data.remote.hackerone.HackerOneAuthInterceptor
import com.bountyos.data.remote.hackerone.HackerOneProvider
import com.bountyos.data.remote.intigriti.IntigritiApi
import com.bountyos.data.remote.intigriti.IntigritiAuthInterceptor
import com.bountyos.data.remote.intigriti.IntigritiProvider
import com.bountyos.data.remote.yeswehack.YesWeHackApi
import com.bountyos.data.remote.yeswehack.YesWeHackAuthInterceptor
import com.bountyos.data.remote.yeswehack.YesWeHackProvider
import com.bountyos.data.security.CredentialStore
import com.bountyos.data.security.KeystoreCredentialStore
import com.bountyos.domain.provider.BountyProvider
import com.bountyos.domain.repository.AiChatRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * 数据层依赖的 Hilt 绑定。
 *
 * 提供数据库、凭证存储、两个平台的 Retrofit 客户端与只读 provider。
 * HTTP 日志仅在 debug 构建启用，且级别为 BASIC（不含 header/body），
 * 避免泄露 token 或报告内容。
 */
@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideCredentialStore(@ApplicationContext context: Context): CredentialStore =
        KeystoreCredentialStore(context)

    @Provides
    @Singleton
    fun provideAiChatRepository(client: OpenAiClient): AiChatRepository = client

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): BountyOsDatabase =
        Room.databaseBuilder(context, BountyOsDatabase::class.java, DATABASE_NAME).build()

    @Provides
    @Singleton
    fun provideSubmissionDao(database: BountyOsDatabase): SubmissionDao = database.submissionDao()

    @Provides
    @Singleton
    fun provideProgramDao(database: BountyOsDatabase): ProgramDao = database.programDao()

    @Provides
    @Singleton
    fun provideActivityDao(database: BountyOsDatabase): ActivityDao = database.activityDao()

    @Provides
    @Singleton
    fun provideIntegrationDao(database: BountyOsDatabase): IntegrationDao = database.integrationDao()

    @Provides
    @Singleton
    fun provideSyncStateDao(database: BountyOsDatabase): SyncStateDao = database.syncStateDao()

    @Provides
    @Singleton
    @HackerOne
    fun provideHackerOneApi(
        credentialStore: CredentialStore,
        @ApplicationContext context: Context,
    ): HackerOneApi {
        val clientBuilder = OkHttpClient.Builder()
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .addInterceptor(HackerOneAuthInterceptor(credentialStore))
        if (isDebuggable(context)) clientBuilder.addInterceptor(basicLogging())
        return Retrofit.Builder()
            .baseUrl(HACKERONE_BASE_URL)
            .client(clientBuilder.build())
            .addConverterFactory(ApiJson.asConverterFactory(JSON_MEDIA_TYPE.toMediaType()))
            .build()
            .create(HackerOneApi::class.java)
    }

    @Provides
    @Singleton
    @Bugcrowd
    fun provideBugcrowdApi(
        credentialStore: CredentialStore,
        @ApplicationContext context: Context,
    ): BugcrowdApi {
        val clientBuilder = OkHttpClient.Builder()
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .addInterceptor(BugcrowdAuthInterceptor(credentialStore))
            .addInterceptor(BugcrowdContentTypeInterceptor())
        if (isDebuggable(context)) clientBuilder.addInterceptor(basicLogging())
        return Retrofit.Builder()
            .baseUrl(BUGCROWD_BASE_URL)
            .client(clientBuilder.build())
            .addConverterFactory(ApiJson.asConverterFactory(JSON_MEDIA_TYPE.toMediaType()))
            .build()
            .create(BugcrowdApi::class.java)
    }

    @Provides
    @Singleton
    @HackerOne
    fun provideHackerOneProvider(
        @HackerOne api: HackerOneApi,
        credentialStore: CredentialStore,
    ): BountyProvider = HackerOneProvider(api, credentialStore)

    @Provides
    @Singleton
    @Bugcrowd
    fun provideBugcrowdProvider(
        @Bugcrowd api: BugcrowdApi,
        credentialStore: CredentialStore,
    ): BountyProvider = BugcrowdProvider(api, credentialStore)

    @Provides
    @Singleton
    @Intigriti
    fun provideIntigritiApi(
        credentialStore: CredentialStore,
        @ApplicationContext context: Context,
    ): IntigritiApi {
        val clientBuilder = OkHttpClient.Builder()
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .addInterceptor(IntigritiAuthInterceptor(credentialStore))
        if (isDebuggable(context)) clientBuilder.addInterceptor(basicLogging())
        return Retrofit.Builder()
            .baseUrl(INTIGRITI_BASE_URL)
            .client(clientBuilder.build())
            .addConverterFactory(ApiJson.asConverterFactory(JSON_MEDIA_TYPE.toMediaType()))
            .build()
            .create(IntigritiApi::class.java)
    }

    @Provides
    @Singleton
    @YesWeHack
    fun provideYesWeHackApi(
        credentialStore: CredentialStore,
        @ApplicationContext context: Context,
    ): YesWeHackApi {
        val clientBuilder = OkHttpClient.Builder()
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .addInterceptor(YesWeHackAuthInterceptor(credentialStore))
        if (isDebuggable(context)) clientBuilder.addInterceptor(basicLogging())
        return Retrofit.Builder()
            .baseUrl(YESWEHACK_BASE_URL)
            .client(clientBuilder.build())
            .addConverterFactory(ApiJson.asConverterFactory(JSON_MEDIA_TYPE.toMediaType()))
            .build()
            .create(YesWeHackApi::class.java)
    }

    @Provides
    @Singleton
    @Intigriti
    fun provideIntigritiProvider(@Intigriti api: IntigritiApi): BountyProvider =
        IntigritiProvider(api)

    @Provides
    @Singleton
    @YesWeHack
    fun provideYesWeHackProvider(@YesWeHack api: YesWeHackApi): BountyProvider =
        YesWeHackProvider(api)

    private fun isDebuggable(context: Context): Boolean =
        (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0

    private fun basicLogging(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }

    private const val DATABASE_NAME = "bountyos.db"
    private const val TIMEOUT_SECONDS = 30L
    private const val HACKERONE_BASE_URL = "https://api.hackerone.com/v1/"
    private const val BUGCROWD_BASE_URL = "https://api.bugcrowd.com/"
    private const val INTIGRITI_BASE_URL = "https://api.intigriti.com/external/company/"
    private const val YESWEHACK_BASE_URL = "https://api.yeswehack.com/"
    private const val JSON_MEDIA_TYPE = "application/json"
}
