package com.bountyos.ui.theme

import androidx.compose.ui.graphics.Color
import com.bountyos.domain.model.SubmissionStatus

/*
 * 规范化状态 -> 颜色的语义映射。
 *
 * 颜色仅作为辅助，不承担唯一信息职责；状态文本始终伴随展示，
 * 以满足无障碍要求。深色与浅色主题各用一套语义色，保证对比度。
 */
fun statusColor(status: SubmissionStatus, isDark: Boolean): Color = when (status) {
    SubmissionStatus.OPEN -> if (isDark) TextSecondary else LightTextSecondary
    SubmissionStatus.TRIAGED -> if (isDark) InfoBlue else LightInfoBlue
    SubmissionStatus.ACTION_REQUIRED -> if (isDark) WarningAmber else LightWarningAmber
    SubmissionStatus.RETESTING -> if (isDark) WarningAmber else LightWarningAmber
    SubmissionStatus.RESOLVED -> if (isDark) SuccessGreen else LightSuccessGreen
    SubmissionStatus.REJECTED -> if (isDark) ErrorRed else LightErrorRed
    SubmissionStatus.DUPLICATE -> if (isDark) TextTertiary else LightTextSecondary
    SubmissionStatus.INFORMATIVE -> if (isDark) TextSecondary else LightTextSecondary
    SubmissionStatus.UNKNOWN -> if (isDark) TextTertiary else LightTextSecondary
}
