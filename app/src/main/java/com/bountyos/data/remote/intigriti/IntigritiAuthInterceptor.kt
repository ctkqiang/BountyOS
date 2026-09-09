package com.bountyos.data.remote.intigriti

import com.bountyos.data.security.CredentialStore
import com.bountyos.domain.model.Provider
import okhttp3.Interceptor
import okhttp3.Response

/**
 * 为 Intigriti 请求注入 `Bearer` 鉴权头。
 *
 * Intigriti 使用静态、非过期的 API access token，鉴权方式为
 * `Authorization: Bearer <token>`，并要求 `Accept: application/json`。
 * token 每次请求时从 [CredentialStore] 动态读取。
 */
class IntigritiAuthInterceptor(
    private val credentialStore: CredentialStore,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val credential = credentialStore.load(Provider.INTIGRITI)
        val builder = chain.request().newBuilder()
            .header("Accept", "application/json")
        credential?.let { builder.header("Authorization", "Bearer ${it.token}") }
        return chain.proceed(builder.build())
    }
}
