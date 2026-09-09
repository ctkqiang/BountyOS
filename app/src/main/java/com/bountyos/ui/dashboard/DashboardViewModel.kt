package com.bountyos.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bountyos.domain.aggregation.DashboardStats
import com.bountyos.domain.aggregation.DashboardStatsCalculator
import com.bountyos.domain.repository.IntegrationRepository
import com.bountyos.domain.repository.SubmissionRepository
import com.bountyos.domain.repository.SyncCoordinator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Dashboard 屏幕状态。
 */
data class DashboardUiState(
    val isLoading: Boolean = true,
    val hasIntegrations: Boolean = false,
    val stats: DashboardStats? = null,
)

/**
 * Dashboard 的 ViewModel。
 *
 * 订阅本地提交与集成状态，并计算统计。统计是纯函数 [DashboardStatsCalculator]
 * 的产物，仅在数据变化时重算。
 */
@HiltViewModel
class DashboardViewModel @Inject constructor(
    submissionRepository: SubmissionRepository,
    integrationRepository: IntegrationRepository,
    private val syncCoordinator: SyncCoordinator,
) : ViewModel() {

    val uiState: StateFlow<DashboardUiState> = combine(
        submissionRepository.observeSubmissions(),
        integrationRepository.observeIntegrations(),
    ) { submissions, integrations ->
        DashboardUiState(
            isLoading = false,
            hasIntegrations = integrations.any { it.connected },
            stats = DashboardStatsCalculator.calculate(submissions),
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        initialValue = DashboardUiState(),
    )

    /** 触发一次手动同步。 */
    fun synchronize() {
        viewModelScope.launch {
            syncCoordinator.synchronize()
        }
    }

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
