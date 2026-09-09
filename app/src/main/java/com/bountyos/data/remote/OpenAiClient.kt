package com.bountyos.data.remote

import com.bountyos.data.settings.AiConfigStore
import com.bountyos.domain.model.AiMessage
import com.bountyos.domain.repository.AiChatRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * OpenAI-compatible Chat Completions 客户端。
 *
 * endpoint 与 apiKey 由 [AiConfigStore] 动态提供，因此未使用 Retrofit
 * 的固定 baseUrl，而是直接以 OkHttp 构造请求。读超时设为 120s 以容纳
 * 较慢的模型推理。
 */
@Singleton
class OpenAiClient @Inject constructor(
    private val configStore: AiConfigStore,
) : AiChatRepository {

    private val client = OkHttpClient.Builder()
        .connectTimeout(CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .readTimeout(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .build()

    override suspend fun chat(messages: List<AiMessage>): String = withContext(Dispatchers.IO) {
        val config = configStore.config.first() ?: throw IllegalStateException("AI 尚未配置")

        val requestBody = ChatCompletionRequest(
            model = config.model,
            messages = messages.map {
                ChatMessageDto(role = it.role.name.lowercase(), content = it.content)
            },
        )
        val body = ApiJson.encodeToString(ChatCompletionRequest.serializer(), requestBody)
            .toRequestBody(JSON_MEDIA_TYPE)

        val request = Request.Builder()
            .url(config.endpoint.trimEnd('/') + CHAT_COMPLETIONS_PATH)
            .addHeader("Authorization", "Bearer ${config.apiKey}")
            .post(body)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw IOException("AI 请求失败：HTTP ${response.code}")
            }
            val text = response.body?.string() ?: throw IOException("AI 返回空响应")
            ApiJson.decodeFromString(ChatCompletionResponse.serializer(), text)
                .choices
                .firstOrNull()
                ?.message
                ?.content
                .orEmpty()
        }
    }

    private companion object {
        const val CONNECT_TIMEOUT_SECONDS = 30L
        const val READ_TIMEOUT_SECONDS = 120L
        const val CHAT_COMPLETIONS_PATH = "/chat/completions"
        val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()
    }
}
