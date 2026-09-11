package com.bountyos.data.repository

import android.content.Context
import com.bountyos.data.remote.ApiJson
import com.bountyos.data.remote.mitre.MitreAttackApi
import com.bountyos.data.remote.mitre.MitreStixParser
import com.bountyos.domain.model.MitreMatrix
import com.bountyos.domain.model.MitreTactic
import com.bountyos.domain.model.MitreTechnique
import com.bountyos.domain.repository.MitreRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * MITRE ATT&CK 仓库的默认实现。
 *
 * 下载的 STIX bundle 只作为中间产物，解析后立刻删除；真正保留的是
 * 精简后的矩阵缓存（战术 + 技术的少量字段），体积从数十 MB 降到
 * 数百 KB，使后续启动无需联网即可浏览。
 */
@Singleton
class MitreRepositoryImpl @Inject constructor(
    private val api: MitreAttackApi,
    @ApplicationContext private val context: Context,
) : MitreRepository {

    private val mutex = Mutex()

    @Volatile
    private var memory: MitreMatrix? = null

    private val compactCache: File
        get() = File(context.filesDir, COMPACT_CACHE_FILE)

    private val bundleFile: File
        get() = File(context.cacheDir, BUNDLE_FILE)

    override suspend fun loadCached(): MitreMatrix? {
        memory?.let { return it }
        return mutex.withLock {
            memory?.let { return@withLock it }
            withContext(Dispatchers.IO) { readCache() }?.also { memory = it }
        }
    }

    override suspend fun refresh(): MitreMatrix = mutex.withLock {
        withContext(Dispatchers.IO) {
            val ref = api.resolveLatestEnterpriseBundle()
            api.downloadBundle(ref.url, bundleFile)
            val matrix = try {
                MitreStixParser.parse(bundleFile, ref.version)
            } finally {
                // 数据包体积大，解析完成后立即清理，避免长期占用存储。
                bundleFile.delete()
            }
            writeCache(matrix)
            memory = matrix
            matrix
        }
    }

    private fun readCache(): MitreMatrix? {
        if (!compactCache.exists()) return null
        return runCatching {
            ApiJson
                .decodeFromString(MitreMatrixDto.serializer(), compactCache.readText())
                .toDomain()
        }.getOrNull()
    }

    private fun writeCache(matrix: MitreMatrix) {
        compactCache.writeText(
            ApiJson.encodeToString(MitreMatrixDto.serializer(), matrix.toDto())
        )
    }

    private companion object {
        const val COMPACT_CACHE_FILE = "mitre_matrix.json"
        const val BUNDLE_FILE = "mitre/enterprise-attack.json"
    }
}

@Serializable
private data class MitreMatrixDto(
    val version: String,
    val tactics: List<MitreTacticDto> = emptyList(),
    val techniques: List<MitreTechniqueDto> = emptyList(),
)

@Serializable
private data class MitreTacticDto(
    val id: String,
    val shortName: String,
    val name: String,
)

@Serializable
private data class MitreTechniqueDto(
    val id: String,
    val name: String,
    val isSubTechnique: Boolean = false,
    val tacticShortNames: List<String> = emptyList(),
    val platforms: List<String> = emptyList(),
)

private fun MitreMatrixDto.toDomain(): MitreMatrix = MitreMatrix(
    version = version,
    tactics = tactics.map { MitreTactic(id = it.id, shortName = it.shortName, name = it.name) },
    techniques = techniques.map {
        MitreTechnique(
            id = it.id,
            name = it.name,
            isSubTechnique = it.isSubTechnique,
            tacticShortNames = it.tacticShortNames,
            platforms = it.platforms,
        )
    },
)

private fun MitreMatrix.toDto(): MitreMatrixDto = MitreMatrixDto(
    version = version,
    tactics = tactics.map { MitreTacticDto(id = it.id, shortName = it.shortName, name = it.name) },
    techniques = techniques.map {
        MitreTechniqueDto(
            id = it.id,
            name = it.name,
            isSubTechnique = it.isSubTechnique,
            tacticShortNames = it.tacticShortNames,
            platforms = it.platforms,
        )
    },
)
