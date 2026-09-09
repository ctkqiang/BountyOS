package com.bountyos.ui.hai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bountyos.domain.model.AiMessage
import com.bountyos.domain.model.ChatRole
import com.bountyos.domain.repository.AiChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Hai（AI 助手）聊天面板的 ViewModel。
 *
 * 维护对话消息列表，并将用户输入与历史一并提交给 [AiChatRepository]
 * 完成补全。鉴权复用 HackerOne 连接 token。
 */
@HiltViewModel
class HaiViewModel @Inject constructor(
    private val aiChatRepository: AiChatRepository,
) : ViewModel() {

    private val _messages = MutableStateFlow<List<AiMessage>>(emptyList())
    val messages: StateFlow<List<AiMessage>> = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    /** 发送一条用户消息并请求助手回复。 */
    fun send(content: String) {
        val trimmed = content.trim()
        if (trimmed.isEmpty() || _isLoading.value) return
        _messages.update { it + AiMessage(ChatRole.USER, trimmed) }
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val reply = aiChatRepository.chat(_messages.value)
                _messages.update { it + AiMessage(ChatRole.ASSISTANT, reply) }
            } catch (e: Exception) {
                _error.value = e.message ?: "请求失败"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}
