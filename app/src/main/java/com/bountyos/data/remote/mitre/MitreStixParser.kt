package com.bountyos.data.remote.mitre

import android.util.JsonReader
import com.bountyos.domain.model.MitreMatrix
import com.bountyos.domain.model.MitreTactic
import com.bountyos.domain.model.MitreTechnique
import java.io.File
import java.io.Reader

/**
 * MITRE ATT&CK STIX bundle 的流式解析器。
 *
 * 数据包体积可达数十 MB，整体反序列化到内存会有 OOM 风险，因此用
 * [JsonReader] 边读边丢弃：只保留战术与技术两类对象的少量字段，其余
 * 字段（尤其是长篇 `description`）一律 skip。
 *
 * 已撤销（`revoked`）与已废弃（`x_mitre_deprecated`）的对象会被过滤。
 */
internal object MitreStixParser {

    private const val TYPE_TACTIC = "x-mitre-tactic"
    private const val TYPE_ATTACK_PATTERN = "attack-pattern"
    private const val KILL_CHAIN_NAME = "mitre-attack"
    private const val SOURCE_NAME = "mitre-attack"

    fun parse(file: File, version: String): MitreMatrix =
        file.inputStream().bufferedReader().use { parse(it, version) }

    fun parse(input: Reader, version: String): MitreMatrix {
        val tactics = mutableListOf<MitreTactic>()
        val techniques = mutableListOf<MitreTechnique>()

        JsonReader(input).use { reader ->
            reader.isLenient = true
            reader.beginObject()
            while (reader.hasNext()) {
                if (reader.nextName() == "objects") {
                    reader.beginArray()
                    while (reader.hasNext()) {
                        readObject(reader, tactics, techniques)
                    }
                    reader.endArray()
                } else {
                    reader.skipValue()
                }
            }
            reader.endObject()
        }

        return MitreMatrix(version = version, tactics = tactics, techniques = techniques)
    }

    private fun readObject(
        reader: JsonReader,
        tactics: MutableList<MitreTactic>,
        techniques: MutableList<MitreTechnique>,
    ) {
        var type: String? = null
        var name: String? = null
        var shortName: String? = null
        var attackId: String? = null
        var isSubTechnique = false
        var isDeprecated = false
        var isRevoked = false
        val phases = mutableListOf<String>()
        val platforms = mutableListOf<String>()

        reader.beginObject()
        while (reader.hasNext()) {
            when (reader.nextName()) {
                "type" -> type = reader.nextString()
                "name" -> name = reader.nextString()
                "x_mitre_shortname" -> shortName = reader.nextString()
                "x_mitre_is_subtechnique" -> isSubTechnique = reader.nextBoolean()
                "x_mitre_deprecated" -> isDeprecated = reader.nextBoolean()
                "revoked" -> isRevoked = reader.nextBoolean()
                "kill_chain_phases" -> readKillChainPhases(reader, phases)
                "external_references" -> attackId = readAttackId(reader) ?: attackId
                "x_mitre_platforms" -> readStringArray(reader, platforms)
                else -> reader.skipValue()
            }
        }
        reader.endObject()

        if (isDeprecated || isRevoked) return

        val id = attackId ?: return
        val label = name ?: return
        when (type) {
            TYPE_TACTIC -> {
                val short = shortName ?: return
                tactics += MitreTactic(id = id, shortName = short, name = label)
            }

            TYPE_ATTACK_PATTERN -> techniques += MitreTechnique(
                id = id,
                name = label,
                isSubTechnique = isSubTechnique,
                tacticShortNames = phases,
                platforms = platforms,
            )
        }
    }

    /** 读取 `kill_chain_phases`，仅收集 mitre-attack 链上的 phase_name。 */
    private fun readKillChainPhases(reader: JsonReader, out: MutableList<String>) {
        reader.beginArray()
        while (reader.hasNext()) {
            var chain: String? = null
            var phase: String? = null
            reader.beginObject()
            while (reader.hasNext()) {
                when (reader.nextName()) {
                    "kill_chain_name" -> chain = reader.nextString()
                    "phase_name" -> phase = reader.nextString()
                    else -> reader.skipValue()
                }
            }
            reader.endObject()
            if (chain == KILL_CHAIN_NAME && phase != null) out += phase
        }
        reader.endArray()
    }

    /** 读取 `external_references`，取 mitre-attack 来源的 external_id（即 Txxxx）。 */
    private fun readAttackId(reader: JsonReader): String? {
        var result: String? = null
        reader.beginArray()
        while (reader.hasNext()) {
            var source: String? = null
            var externalId: String? = null
            reader.beginObject()
            while (reader.hasNext()) {
                when (reader.nextName()) {
                    "source_name" -> source = reader.nextString()
                    "external_id" -> externalId = reader.nextString()
                    else -> reader.skipValue()
                }
            }
            reader.endObject()
            if (result == null && source == SOURCE_NAME && externalId != null) {
                result = externalId
            }
        }
        reader.endArray()
        return result
    }

    private fun readStringArray(reader: JsonReader, out: MutableList<String>) {
        reader.beginArray()
        while (reader.hasNext()) out += reader.nextString()
        reader.endArray()
    }
}
