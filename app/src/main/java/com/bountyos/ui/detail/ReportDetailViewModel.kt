package com.bountyos.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bountyos.domain.model.Activity
import com.bountyos.domain.model.Submission
import com.bountyos.domain.repository.SubmissionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * 报告详情屏幕状态。
 */
data class ReportDetailUiState(
    val submission: Submission? = null,
    val activities: List<Activity> = emptyList(),
)

/**
 * 报告详情的 ViewModel。
 *
 * 通过导航参数中的提交 ID 读取本地缓存的详情，并根据提交的外部 ID
 * 订阅其活动时间线。
 */
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ReportDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    submissionRepository: SubmissionRepository,
) : ViewModel() {

    private val submissionId: String = checkNotNull(savedStateHandle[SUBMISSION_ID_ARG])

    val uiState: StateFlow<ReportDetailUiState> = submissionRepository
        .observeSubmission(submissionId)
        .flatMapLatest { submission ->
            val externalId = submission?.externalId.orEmpty()
            submissionRepository.observeActivities(externalId).map { activities ->
                ReportDetailUiState(
                    submission = submission,
                    activities = activities,
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = ReportDetailUiState(),
        )

    companion object {
        const val SUBMISSION_ID_ARG = "submissionId"
    }
}

private const val STOP_TIMEOUT_MILLIS = 5_000L
