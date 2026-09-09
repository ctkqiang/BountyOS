package com.bountyos.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.bountyos.R
import com.bountyos.domain.model.SubmissionStatus

/**
 * 返回规范化状态对应的本地化文案。
 *
 * 供状态徽标、状态筛选 chip 等多处复用，避免重复的 when 映射。
 */
@Composable
fun submissionStatusLabel(status: SubmissionStatus): String = when (status) {
    SubmissionStatus.OPEN -> stringResource(R.string.status_open)
    SubmissionStatus.TRIAGED -> stringResource(R.string.status_triaged)
    SubmissionStatus.ACTION_REQUIRED -> stringResource(R.string.status_action_required)
    SubmissionStatus.RETESTING -> stringResource(R.string.status_retesting)
    SubmissionStatus.RESOLVED -> stringResource(R.string.status_resolved)
    SubmissionStatus.REJECTED -> stringResource(R.string.status_rejected)
    SubmissionStatus.DUPLICATE -> stringResource(R.string.status_duplicate)
    SubmissionStatus.INFORMATIVE -> stringResource(R.string.status_informative)
    SubmissionStatus.UNKNOWN -> stringResource(R.string.status_unknown)
}
