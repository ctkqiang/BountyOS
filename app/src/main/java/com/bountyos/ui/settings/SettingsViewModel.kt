package com.bountyos.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bountyos.data.settings.AiConfigStore
import com.bountyos.data.settings.ThemePreferenceStore
import com.bountyos.domain.model.Integration
import com.bountyos.domain.model.Provider
import com.bountyos.domain.model.ThemeMode
import com.bountyos.domain.repository.IntegrationRepository
import com.bountyos.domain.repository.SyncCoordinator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Settings 屏幕状态。
 */
data class SettingsUiState(
    val integrations: List<Integration> = emptyList(),
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val aiConfigured: Boolean = false,
    val aiEndpoint: String = "",
    val aiModel: String = "",
)

/**
 * Settings（连接管理、主题偏好与 AI 配置）的 ViewModel。
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val integrationRepository: IntegrationRepository,
    private val syncCoordinator: SyncCoordinator,
    private val themePreferenceStore: ThemePreferenceStore,
    private val aiConfigStore: AiConfigStore,
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = combine(
        integrationRepository.observeIntegrations(),
        themePreferenceStore.themeMode,
        aiConfigStore.config,
    ) { integrations, themeMode, aiConfig ->
        SettingsUiState(
            integrations = integrations,
            themeMode = themeMode,
            aiConfigured = aiConfig != null,
            aiEndpoint = aiConfig?.endpoint ?: "",
            aiModel = aiConfig?.model ?: "",
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        initialValue = SettingsUiState(),
    )

    /**
     * 连接平台。
     *
     * @param onResult 连接结果回调，供 UI 展示成功或失败提示。
     */
    fun connect(
        provider: Provider,
        username: String?,
        token: String,
        onResult: (Result<Unit>) -> Unit,
    ) {
        viewModelScope.launch {
            val result = integrationRepository.connect(provider, username, token)
            onResult(result)
            if (result.isSuccess) {
                // 连接成功后立即同步一次，让用户尽快看到数据。
                syncCoordinator.synchronize(provider)
            }
        }
    }

    fun disconnect(provider: Provider) {
        viewModelScope.launch {
            integrationRepository.disconnect(provider)
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            themePreferenceStore.setThemeMode(mode)
        }
    }

    fun saveAiConfig(endpoint: String, model: String, apiKey: String) {
        viewModelScope.launch {
            aiConfigStore.save(endpoint.trim(), model.trim(), apiKey.trim())
        }
    }

    fun clearAiConfig() {
        viewModelScope.launch {
            aiConfigStore.clear()
        }
    }

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
