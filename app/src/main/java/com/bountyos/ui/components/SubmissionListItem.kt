package com.bountyos.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.bountyos.R
import com.bountyos.domain.model.Provider
import com.bountyos.domain.model.Submission
import com.bountyos.domain.model.SubmissionStatus
import com.bountyos.ui.theme.statusColor

/**
 * 报告列表项。
 *
 * 展示 provider、标题、外部 ID、状态与奖励。用于 Reports / Triage /
 * Dashboard 最近活动等列表，避免重复实现。
 */
@Composable
fun SubmissionListItem(
    submission: Submission,
    onClick: () -> Unit,
) {
    ListItem(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        headlineContent = {
            Text(
                text = submission.title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        },
        supportingContent = {
            Column {
                Text(
                    text = "#${submission.externalId}" +
                        (submission.programName?.let { " · $it" } ?: ""),
                    style = MaterialTheme.typography.labelMedium,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.width(4.dp))
                StatusChip(status = submission.status, providerStatus = submission.providerStatus)
            }
        },
        leadingContent = {
            ProviderBadge(provider = submission.provider)
        },
        trailingContent = {
            submission.reward?.let { reward ->
                Text(
                    text = "${reward.amount} ${reward.currency.orEmpty()}".trim(),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        },
    )
}

/**
 * 平台缩写徽标（H1 / BC）。
 */
@Composable
fun ProviderBadge(provider: Provider) {
    val label = when (provider) {
        Provider.HACKERONE -> "H1"
        Provider.BUGCROWD -> "BC"
    }
    Text(
        text = label,
        style = MaterialTheme.typography.labelMedium,
        fontFamily = FontFamily.Monospace,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(horizontal = 4.dp),
    )
}

/**
 * 状态徽标：展示规范化状态文案，括号内保留平台原始状态。
 */
@Composable
fun StatusChip(status: SubmissionStatus, providerStatus: String) {
    val canonical = when (status) {
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
    val display = if (providerStatus.isNotBlank() && status != SubmissionStatus.UNKNOWN) {
        "$canonical ($providerStatus)"
    } else {
        canonical
    }
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    Text(
        text = display,
        style = MaterialTheme.typography.labelMedium,
        color = statusColor(status, isDark),
    )
}
