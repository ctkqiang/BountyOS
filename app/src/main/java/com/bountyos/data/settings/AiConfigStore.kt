package com.bountyos.data.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.bountyos.domain.model.AiConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.aiConfigDataStore: DataStore<Preferences> by preferencesDataStore(name = "ai_config")

/**
 * AI 助手（Hai）配置存储。
 *
 * endpoint 与 model 存于 DataStore，apiKey 由 [AiApiKeyStore] 经
 * Keystore 加密保存。只有三者齐全时才对外暴露有效配置，否则返回 null。
 */
@Singleton
class AiConfigStore @Inject constructor(
    @ApplicationContext private val context: Context,
    private val apiKeyStore: AiApiKeyStore,
) {

    val config: Flow<AiConfig?> = context.aiConfigDataStore.data.map { preferences ->
        val endpoint = preferences[ENDPOINT_KEY] ?: return@map null
        val model = preferences[MODEL_KEY] ?: return@map null
        val apiKey = apiKeyStore.read() ?: return@map null
        AiConfig(endpoint = endpoint, model = model, apiKey = apiKey)
    }

    suspend fun save(endpoint: String, model: String, apiKey: String) {
        apiKeyStore.save(apiKey)
        context.aiConfigDataStore.edit { preferences ->
            preferences[ENDPOINT_KEY] = endpoint
            preferences[MODEL_KEY] = model
        }
    }

    suspend fun clear() {
        apiKeyStore.clear()
        context.aiConfigDataStore.edit { preferences ->
            preferences.remove(ENDPOINT_KEY)
            preferences.remove(MODEL_KEY)
        }
    }

    private companion object {
        val ENDPOINT_KEY = stringPreferencesKey("ai_endpoint")
        val MODEL_KEY = stringPreferencesKey("ai_model")
    }
}
