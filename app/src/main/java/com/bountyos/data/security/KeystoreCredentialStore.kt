package com.bountyos.data.security

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import com.bountyos.domain.model.Provider
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * 基于 Android Keystore 的凭证存储实现。
 *
 * 使用 AES/GCM/NoPadding 加密凭证，密钥由 Android Keystore 硬件保护
 * 且不可导出。密文（IV + 密文）以 Base64 存入私有 SharedPreferences，
 * 仅本应用可读。
 */
class KeystoreCredentialStore(
    context: Context,
) : CredentialStore {

    private val preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val keyStore: KeyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }

    override fun save(provider: Provider, credential: Credential) {
        val plaintext = Credential.toPlaintext(credential)
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, getOrCreateKey())
        val encrypted = cipher.doFinal(plaintext.toByteArray(Charsets.UTF_8))
        val encoded = Base64.encodeToString(cipher.iv, Base64.NO_WRAP) +
            ":" +
            Base64.encodeToString(encrypted, Base64.NO_WRAP)
        preferences.edit().putString(provider.name, encoded).apply()
    }

    override fun load(provider: Provider): Credential? {
        val encoded = preferences.getString(provider.name, null) ?: return null
        return runCatching {
            val parts = encoded.split(":")
            require(parts.size == 2) { "Malformed credential payload" }
            val iv = Base64.decode(parts[0], Base64.NO_WRAP)
            val encrypted = Base64.decode(parts[1], Base64.NO_WRAP)
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.DECRYPT_MODE, getOrCreateKey(), GCMParameterSpec(128, iv))
            val plaintext = cipher.doFinal(encrypted).toString(Charsets.UTF_8)
            Credential.fromPlaintext(plaintext)
        }.getOrNull()
    }

    override fun clear(provider: Provider) {
        preferences.edit().remove(provider.name).apply()
    }

    /**
     * 获取或创建用于凭证加密的 AES 密钥。
     *
     * 密钥一旦生成便持久化于 Keystore；应用卸载时会随 Keystore 一并清除。
     */
    private fun getOrCreateKey(): SecretKey {
        val existing = keyStore.getEntry(KEY_ALIAS, null) as? KeyStore.SecretKeyEntry
        if (existing != null) return existing.secretKey

        val generator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
        generator.init(
            KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT,
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .build(),
        )
        return generator.generateKey()
    }

    private companion object {
        const val PREFS_NAME = "bountyos_credentials"
        const val KEY_ALIAS = "bountyos_credential_key"
        const val ANDROID_KEYSTORE = "AndroidKeyStore"
        const val TRANSFORMATION = "AES/GCM/NoPadding"
    }
}
