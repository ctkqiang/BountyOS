package com.bountyos.data.security

import com.bountyos.domain.model.Provider

/**
 * 平台凭证。
 *
 * 凭证是敏感信息，绝不写入 Room、日志或任何明文持久化。仅通过
 * [CredentialStore] 以 Android Keystore 加密后保存。
 *
 * @param username HackerOne 使用 `username:token` 的 Basic Auth，需要用户名；
 *                 Bugcrowd 使用 `Token` 头，无需用户名，因此可为空。
 * @param token    平台访问令牌。
 */
data class Credential(
    val username: String?,
    val token: String,
) {
    companion object {
        /** 用户名与令牌之间的分隔符（凭证中不会出现的字符）。 */
        private const val SEPARATOR = "\u0000"

        internal fun toPlaintext(credential: Credential): String =
            (credential.username.orEmpty()) + SEPARATOR + credential.token

        internal fun fromPlaintext(plaintext: String): Credential {
            val parts = plaintext.split(SEPARATOR)
            return Credential(
                username = parts.getOrNull(0)?.takeIf { it.isNotEmpty() },
                token = parts.getOrNull(1).orEmpty(),
            )
        }
    }
}

/**
 * 平台凭证的安全存储契约。
 *
 * 实现必须使用 Android Keystore 或等效的安全机制，禁止明文存储。
 */
interface CredentialStore {

    fun save(provider: Provider, credential: Credential)

    fun load(provider: Provider): Credential?

    fun clear(provider: Provider)
}
