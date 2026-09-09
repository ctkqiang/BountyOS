package com.bountyos.data.remote

import kotlinx.serialization.json.Json

/*
 * 远程层共享的 JSON 配置。
 *
 * `ignoreUnknownKeys` 是必须的：JSON:API 响应包含大量未建模字段，
 * 严格模式会导致反序列化失败。该实例同时供 Retrofit converter 与
 * mapper 使用，保证行为一致。
 */
internal val ApiJson: Json = Json {
    ignoreUnknownKeys = true
    isLenient = true
}
