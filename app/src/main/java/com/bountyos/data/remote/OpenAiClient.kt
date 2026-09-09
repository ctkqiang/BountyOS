package com.bountyos.data.remote

import com.bountyos.data.security.CredentialStore
import com.bountyos.domain.model.AiMessage
import com.bountyos.domain.model.Provider
import com.bountyos.domain.repository.AiChatRepository
import kotlinx.coroutines.Dispatchers
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
 * 鉴权复用 HackerOne 的连接 token（Bearer），endpoint 与 model 使用内置
 * 默认值，因此无需单独的 AI 配置。读超时设为 120s 以容纳较慢的模型推理。
 */
@Singleton
class OpenAiClient @Inject constructor(
    private val credentialStore: CredentialStore,
) : AiChatRepository {

    private val client = OkHttpClient.Builder()
        .connectTimeout(CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .readTimeout(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .build()

    override suspend fun chat(messages: List<AiMessage>): String = withContext(Dispatchers.IO) {
        val credential = credentialStore.load(Provider.HACKERONE)
            ?: throw IllegalStateException("HackerOne 尚未连接")

        val requestBody = ChatCompletionRequest(
            model = DEFAULT_MODEL,
            messages = messages.map {
                ChatMessageDto(role = it.role.name.lowercase(), content = it.content)
            },
        )
        val body = ApiJson.encodeToString(ChatCompletionRequest.serializer(), requestBody)
            .toRequestBody(JSON_MEDIA_TYPE)

        val request = Request.Builder()
            .url(DEFAULT_ENDPOINT.trimEnd('/') + CHAT_COMPLETIONS_PATH)
            .addHeader("Authorization", "Bearer ${credential.token}")
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
        const val DEFAULT_ENDPOINT = "https://api.openai.com/v1"
        const val DEFAULT_MODEL = "gpt-4o-mini"
        val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()
    }
}
