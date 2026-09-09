package com.bountyos.ui.theme

import androidx.compose.ui.graphics.Color
import com.bountyos.domain.model.SubmissionStatus

/*
 * 规范化状态 -> 颜色的语义映射。
 *
 * 颜色仅作为辅助，不承担唯一信息职责；状态文本始终伴随展示，
 * 以满足无障碍要求。
 */
fun statusColor(status: SubmissionStatus): Color = when (status) {
    SubmissionStatus.OPEN -> CoolNeutral
    SubmissionStatus.TRIAGED -> InfoBlue
    SubmissionStatus.ACTION_REQUIRED -> WarningAmber
    SubmissionStatus.RETESTING -> WarningAmber
    SubmissionStatus.RESOLVED -> SuccessGreen
    SubmissionStatus.REJECTED -> ErrorRed
    SubmissionStatus.DUPLICATE -> MutedNeutral
    SubmissionStatus.INFORMATIVE -> CoolNeutral
    SubmissionStatus.UNKNOWN -> MutedNeutral
}
