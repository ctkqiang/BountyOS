package com.bountyos.ui.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bountyos.domain.model.Activity
import com.bountyos.domain.repository.SubmissionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * Activity 时间线屏幕状态。
 */
data class ActivityUiState(
    val activities: List<Activity> = emptyList(),
)

/**
 * 统一活动时间线的 ViewModel。
 */
@HiltViewModel
class ActivityViewModel @Inject constructor(
    submissionRepository: SubmissionRepository,
) : ViewModel() {

    val uiState: StateFlow<ActivityUiState> = submissionRepository.observeAllActivities()
        .map { ActivityUiState(activities = it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = ActivityUiState(),
        )

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
