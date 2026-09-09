package com.bountyos.data.remote.yeswehack

import com.bountyos.data.security.CredentialStore
import com.bountyos.domain.model.Provider
import okhttp3.Interceptor
import okhttp3.Response

/**
 * 为 YesWeHack 请求注入 `X-AUTH-TOKEN` 鉴权头。
 *
 * YesWeHack 使用 Personal Access Token（PAT），鉴权方式为
 * `X-AUTH-TOKEN: <token>`，并要求 `Accept: application/json`。
 * token 每次请求时从 [CredentialStore] 动态读取。
 */
class YesWeHackAuthInterceptor(
    private val credentialStore: CredentialStore,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val credential = credentialStore.load(Provider.YESWEHACK)
        val builder = chain.request().newBuilder()
            .header("Accept", "application/json")
        credential?.let { builder.header("X-AUTH-TOKEN", it.token) }
        return chain.proceed(builder.build())
    }
}
