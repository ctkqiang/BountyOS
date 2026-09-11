package com.bountyos.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.bountyos.domain.model.Provider
import com.bountyos.domain.model.Submission
import com.bountyos.domain.model.SubmissionStatus
import com.bountyos.ui.theme.glassBorder
import com.bountyos.ui.theme.statusColor

/**
 * 报告列表项。
 *
 * 卡片式布局：顶部元数据行（平台徽标 + 报告 ID + 奖励），中部标题，
 * 底部状态徽标。用于 Reports / Triage / Dashboard 最近活动等列表。
 */
@Composable
fun SubmissionListItem(
    submission: Submission,
    onClick: () -> Unit,
) {
    val haptics = LocalHapticFeedback.current
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .glassBorder(MaterialTheme.shapes.medium)
            .clickable {
                haptics.performHapticFeedback(Haptics.Tap)
                onClick()
            },
        color = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.medium,
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ProviderBadge(submission.provider)
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "#${submission.externalId}",
                    style = MaterialTheme.typography.labelSmall,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.weight(1f))
                submission.reward?.let { reward ->
                    Text(
                        text = "${reward.amount} ${reward.currency.orEmpty()}".trim(),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
            Text(
                text = submission.title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 8.dp),
            )
            Spacer(Modifier.height(10.dp))
            StatusChip(status = submission.status, providerStatus = submission.providerStatus)
        }
    }
}

/**
 * 平台缩写徽标（H1 / BC / IT / YWH）。
 */
@Composable
fun ProviderBadge(provider: Provider) {
    val label = when (provider) {
        Provider.HACKERONE -> "H1"
        Provider.BUGCROWD -> "BC"
        Provider.INTIGRITI -> "IT"
        Provider.YESWEHACK -> "YWH"
    }
    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer,
        shape = RoundedCornerShape(6.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontFamily = FontFamily.Monospace,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
        )
    }
}

/**
 * 状态徽标：展示规范化状态文案，括号内保留平台原始状态。
 */
@Composable
fun StatusChip(status: SubmissionStatus, providerStatus: String) {
    val canonical = submissionStatusLabel(status)
    val display = if (providerStatus.isNotBlank() && status != SubmissionStatus.UNKNOWN) {
        "$canonical ($providerStatus)"
    } else {
        canonical
    }
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val color = statusColor(status, isDark)
    Surface(
        color = color.copy(alpha = if (isDark) 0.16f else 0.12f),
        shape = RoundedCornerShape(6.dp),
    ) {
        Text(
            text = display,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
        )
    }
}
