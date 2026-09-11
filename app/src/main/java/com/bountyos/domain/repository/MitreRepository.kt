package com.bountyos.domain.repository

import com.bountyos.domain.model.MitreMatrix

/**
 * MITRE ATT&CK 知识库的只读仓库。
 *
 * 数据来自 MITRE 官方的 attack-stix-data 仓库：先读官方索引拿到最新
 * Enterprise 数据包地址，再下载 STIX bundle 并流式解析为矩阵快照。
 * 解析结果以精简格式缓存在本地，避免重复下载与重复解析。
 */
interface MitreRepository {

    /** 读取本地缓存的矩阵快照；无缓存或缓存损坏时返回 null。 */
    suspend fun loadCached(): MitreMatrix?

    /** 重新下载并解析最新数据，成功后覆盖缓存。 */
    suspend fun refresh(): MitreMatrix
}
