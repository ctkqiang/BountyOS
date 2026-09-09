package com.bountyos.data.remote.bugcrowd

import com.bountyos.data.security.CredentialStore
import com.bountyos.domain.model.Provider
import okhttp3.Interceptor
import okhttp3.Response

/**
 * 为 Bugcrowd 请求注入 `Token` 鉴权头与必需的媒体类型头。
 *
 * Bugcrowd 要求 `Accept: application/vnd.bugcrowd+json`，鉴权为
 * `Authorization: Token <token>`。凭证每次请求时从 [CredentialStore]
 * 动态读取。
 */
class BugcrowdAuthInterceptor(
    private val credentialStore: CredentialStore,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val credential = credentialStore.load(Provider.BUGCROWD)
        val builder = chain.request().newBuilder()
            .header("Accept", ACCEPT_MEDIA_TYPE)
        credential?.let { builder.header("Authorization", "Token ${it.token}") }
        return chain.proceed(builder.build())
    }

    private companion object {
        const val ACCEPT_MEDIA_TYPE = "application/vnd.bugcrowd+json"
    }
}

/**
 * 将 Bugcrowd 返回的 vendor 媒体类型归一化为 `application/json`，
 * 以便 kotlinx-serialization converter 正确解析响应体。
 */
class BugcrowdContentTypeInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        val contentType = response.header("Content-Type") ?: return response
        return if (contentType.startsWith("application/vnd.bugcrowd")) {
            response.newBuilder()
                .removeHeader("Content-Type")
                .addHeader("Content-Type", "application/json")
                .build()
        } else {
            response
        }
    }
}
