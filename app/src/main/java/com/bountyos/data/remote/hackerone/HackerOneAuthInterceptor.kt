package com.bountyos.data.remote.hackerone

import android.util.Base64
import com.bountyos.data.security.CredentialStore
import com.bountyos.domain.model.Provider
import okhttp3.Interceptor
import okhttp3.Response

/**
 * 为 HackerOne 请求注入 HTTP Basic 鉴权头。
 *
 * HackerOne 使用 `username:token` 的 Basic Auth。凭证在每次请求时从
 * [CredentialStore] 动态读取，避免把 token 缓存在拦截器字段中。
 */
class HackerOneAuthInterceptor(
    private val credentialStore: CredentialStore,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val credential = credentialStore.load(Provider.HACKERONE)
        val request = chain.request()
        val authorized = credential?.let {
            val encoded = Base64.encodeToString(
                "${it.username.orEmpty()}:${it.token}".toByteArray(Charsets.UTF_8),
                Base64.NO_WRAP,
            )
            request.newBuilder()
                .header("Authorization", "Basic $encoded")
                .build()
        } ?: request
        return chain.proceed(authorized)
    }
}
