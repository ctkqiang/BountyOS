package com.bountyos.domain.model

/**
 * AI 助手（Hai）的连接配置。
 *
 * 使用 OpenAI-compatible Chat Completions 协议，因此可对接 OpenAI、
 * DeepSeek、Kimi、通义千问等任意兼容服务。apiKey 由 Keystore 加密存储，
 * 不进入 DataStore 或日志。
 */
data class AiConfig(
    val endpoint: String,
    val model: String,
    val apiKey: String,
)
