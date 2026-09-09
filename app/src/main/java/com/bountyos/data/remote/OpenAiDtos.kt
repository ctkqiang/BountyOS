package com.bountyos.data.remote

import kotlinx.serialization.Serializable

/** OpenAI-compatible Chat Completions 请求体。 */
@Serializable
data class ChatCompletionRequest(
    val model: String,
    val messages: List<ChatMessageDto>,
)

/** 单条对话消息 DTO。 */
@Serializable
data class ChatMessageDto(
    val role: String,
    val content: String,
)

/** Chat Completions 响应体。 */
@Serializable
data class ChatCompletionResponse(
    val choices: List<ChoiceDto> = emptyList(),
)

/** 单个候选结果。 */
@Serializable
data class ChoiceDto(
    val message: ChatMessageDto? = null,
)
