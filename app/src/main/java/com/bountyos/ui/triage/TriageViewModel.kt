package com.bountyos.ui.triage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bountyos.domain.model.Submission
import com.bountyos.domain.normalization.AttentionCalculator
import com.bountyos.domain.repository.SubmissionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * Triage（Attention Inbox）屏幕状态。
 */
data class TriageUiState(
    val attentionItems: List<Submission> = emptyList(),
)

/**
 * Attention Inbox 的 ViewModel。
 *
 * 只读地筛选出需要研究者关注的提交（需补充信息、待复测）。
 * 用户仍需跳转到原平台操作，BountyOS 不做任何写入。
 */
@HiltViewModel
class TriageViewModel @Inject constructor(
    submissionRepository: SubmissionRepository,
) : ViewModel() {

    val uiState: StateFlow<TriageUiState> = submissionRepository.observeSubmissions()
        .map { submissions ->
            TriageUiState(
                attentionItems = submissions.filter { AttentionCalculator.requiresAttention(it.status) },
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = TriageUiState(),
        )

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
