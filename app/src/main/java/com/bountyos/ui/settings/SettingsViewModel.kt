package com.bountyos.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
)

/**
 * Settings（连接管理与主题偏好）的 ViewModel。
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val integrationRepository: IntegrationRepository,
    private val syncCoordinator: SyncCoordinator,
    private val themePreferenceStore: ThemePreferenceStore,
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = combine(
        integrationRepository.observeIntegrations(),
        themePreferenceStore.themeMode,
    ) { integrations, themeMode ->
        SettingsUiState(
            integrations = integrations,
            themeMode = themeMode,
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

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
