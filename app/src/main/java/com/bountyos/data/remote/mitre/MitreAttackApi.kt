package com.bountyos.data.remote.mitre

import com.bountyos.data.remote.ApiJson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/** 已解析出的 Enterprise ATT&CK 数据包版本与下载地址。 */
data class MitreBundleRef(
    val version: String,
    val url: String,
)

/**
 * MITRE attack-stix-data 仓库的只读 HTTP 客户端。
 *
 * 仓库与索引均公开、无需鉴权。数据包体积可达数十 MB，因此下载走
 * 流式写盘，不整体读入内存。
 */
@Singleton
class MitreAttackApi @Inject constructor() {

    private val client = OkHttpClient.Builder()
        .connectTimeout(CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .readTimeout(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .build()

    /**
     * 从官方索引中解析出最新的 Enterprise 数据包。
     *
     * 索引按版本倒序列出各集合，这里取第一个 URL 命中
     * `enterprise-attack` 的版本，避免依赖集合名称或顺序。
     */
    suspend fun resolveLatestEnterpriseBundle(): MitreBundleRef = withContext(Dispatchers.IO) {
        val index = ApiJson.decodeFromString(AttackIndexDto.serializer(), execute(INDEX_URL))
        index.collections
            .flatMap { it.versions }
            .firstOrNull { it.url.contains(ENTERPRISE_PATH) && it.version.isNotBlank() }
            ?.let { MitreBundleRef(version = it.version, url = it.url) }
            ?: throw IOException("Enterprise ATT&CK bundle not found in index")
    }

    /** 将数据包流式下载到 [destination]。 */
    suspend fun downloadBundle(url: String, destination: File) = withContext(Dispatchers.IO) {
        destination.parentFile?.mkdirs()
        val request = Request.Builder().url(url).build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw IOException("ATT&CK HTTP ${response.code}")
            }
            val body = response.body ?: throw IOException("ATT&CK empty response")
            body.byteStream().use { input ->
                destination.outputStream().use { output -> input.copyTo(output) }
            }
        }
    }

    private fun execute(url: String): String {
        val request = Request.Builder().url(url).build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw IOException("ATT&CK HTTP ${response.code}")
            }
            return response.body?.string().orEmpty()
        }
    }

    private companion object {
        const val CONNECT_TIMEOUT_SECONDS = 30L
        const val READ_TIMEOUT_SECONDS = 120L
        const val ENTERPRISE_PATH = "enterprise-attack"
        const val INDEX_URL =
            "https://raw.githubusercontent.com/mitre-attack/attack-stix-data/master/index.json"
    }
}

@Serializable
private data class AttackIndexDto(
    val collections: List<AttackCollectionDto> = emptyList(),
)

@Serializable
private data class AttackCollectionDto(
    val versions: List<AttackVersionDto> = emptyList(),
)

@Serializable
private data class AttackVersionDto(
    val version: String = "",
    val url: String = "",
)
