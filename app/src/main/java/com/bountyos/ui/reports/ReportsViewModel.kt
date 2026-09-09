package com.bountyos.ui.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bountyos.domain.model.Provider
import com.bountyos.domain.model.Submission
import com.bountyos.domain.model.SubmissionStatus
import com.bountyos.domain.repository.SubmissionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

/**
 * Reports 屏幕状态。
 */
data class ReportsUiState(
    val submissions: List<Submission> = emptyList(),
    val query: String = "",
    val selectedProvider: Provider? = null,
    val selectedStatus: SubmissionStatus? = null,
)

/**
 * Reports 列表的 ViewModel。
 *
 * 支持本地搜索与筛选（provider、status）。搜索与筛选仅作用于本地
 * 缓存数据，不触发任何网络请求。
 */
@HiltViewModel
class ReportsViewModel @Inject constructor(
    submissionRepository: SubmissionRepository,
) : ViewModel() {

    private val query = MutableStateFlow("")
    private val selectedProvider = MutableStateFlow<Provider?>(null)
    private val selectedStatus = MutableStateFlow<SubmissionStatus?>(null)

    val uiState: StateFlow<ReportsUiState> = combine(
        submissionRepository.observeSubmissions(),
        query,
        selectedProvider,
        selectedStatus,
    ) { submissions, currentQuery, provider, status ->
        ReportsUiState(
            submissions = filter(submissions, currentQuery, provider, status),
            query = currentQuery,
            selectedProvider = provider,
            selectedStatus = status,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        initialValue = ReportsUiState(),
    )

    fun onQueryChange(value: String) = query.update { value }

    fun onProviderSelect(value: Provider?) = selectedProvider.update { value }

    fun onStatusSelect(value: SubmissionStatus?) = selectedStatus.update { value }

    private fun filter(
        submissions: List<Submission>,
        currentQuery: String,
        provider: Provider?,
        status: SubmissionStatus?,
    ): List<Submission> = submissions
        .filter { provider == null || it.provider == provider }
        .filter { status == null || it.status == status }
        .filter { submission ->
            val keyword = currentQuery.trim()
            keyword.isEmpty() ||
                submission.title.contains(keyword, ignoreCase = true) ||
                submission.externalId.contains(keyword, ignoreCase = true) ||
                (submission.programName?.contains(keyword, ignoreCase = true) == true)
        }

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
