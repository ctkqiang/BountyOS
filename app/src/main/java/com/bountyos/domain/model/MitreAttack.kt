package com.bountyos.domain.model

/**
 * MITRE ATT&CK 的单个战术（Tactic），如 Initial Access（TA0001）。
 *
 * [shortName] 是 STIX 中的 `x_mitre_shortname`，技术通过
 * `kill_chain_phases.phase_name` 与它关联。
 */
data class MitreTactic(
    val id: String,
    val shortName: String,
    val name: String,
)

/**
 * MITRE ATT&CK 的单个技术（Technique）或子技术（Sub-technique）。
 */
data class MitreTechnique(
    val id: String,
    val name: String,
    val isSubTechnique: Boolean,
    val tacticShortNames: List<String>,
    val platforms: List<String>,
)

/**
 * 一次 Enterprise ATT&CK 矩阵快照。
 *
 * 由官方 STIX bundle 解析而来，仅保留战术与技术的最小字段集，
 * 不含长篇描述（描述会使缓存体积膨胀数十倍）。
 */
data class MitreMatrix(
    val version: String,
    val tactics: List<MitreTactic>,
    val techniques: List<MitreTechnique>,
)
