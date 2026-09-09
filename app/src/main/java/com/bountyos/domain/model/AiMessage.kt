package com.bountyos.domain.model

/** 聊天消息角色。 */
enum class ChatRole { USER, ASSISTANT }

/** 一条 AI 对话消息。 */
data class AiMessage(
    val role: ChatRole,
    val content: String,
)
