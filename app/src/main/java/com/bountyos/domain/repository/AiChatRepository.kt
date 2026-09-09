package com.bountyos.domain.repository

import com.bountyos.domain.model.AiMessage

/**
 * AI 聊天仓库抽象。
 *
 * 仅暴露单次补全能力：给定历史消息，返回助手回复文本。具体模型与
 * 服务由实现方（OpenAI-compatible 客户端）决定。
 */
interface AiChatRepository {
    suspend fun chat(messages: List<AiMessage>): String
}
