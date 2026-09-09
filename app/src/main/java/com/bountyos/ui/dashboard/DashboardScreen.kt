package com.bountyos.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.bountyos.R
import com.bountyos.domain.aggregation.DashboardStats
import com.bountyos.domain.aggregation.RewardTotal
import com.bountyos.ui.components.EmptyState
import com.bountyos.ui.components.SubmissionListItem
import com.bountyos.ui.navigation.Route

/**
 * Dashboard 主屏幕。
 *
 * 顶部展示总奖励，下方展示提交统计与最近提交。未连接任何平台时
 * 显示 onboarding 空状态。
 */
@Composable
fun DashboardScreen(navController: NavController) {
    val viewModel: DashboardViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    when {
        state.isLoading -> LoadingContent()
        !state.hasIntegrations -> {
            EmptyState(
                title = stringResource(R.string.onboarding_title),
                description = stringResource(R.string.onboarding_description),
                action = {
                    Button(onClick = { navController.navigate(Route.SETTINGS) }) {
                        Text(stringResource(R.string.connect_platform))
                    }
                },
            )
        }
        else -> DashboardContent(
            stats = state.stats,
            onOpenSubmission = { id -> navController.navigate(Route.reportDetail(id)) },
        )
    }
}

@Composable
private fun LoadingContent() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun DashboardContent(
    stats: DashboardStats?,
    onOpenSubmission: (String) -> Unit,
) {
    if (stats == null) return
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Column {
                Text(
                    text = stringResource(R.string.total_bounties),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Rewards(stats.totalRewards)
            }
        }
        item { HorizontalDivider() }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                Metric(label = stringResource(R.string.submissions), value = stats.submissionCount.toString())
                Metric(label = stringResource(R.string.attention), value = stats.attentionCount.toString())
            }
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                Metric(label = stringResource(R.string.hackerone), value = stats.hackerOneCount.toString())
                Metric(label = stringResource(R.string.bugcrowd), value = stats.bugcrowdCount.toString())
            }
        }
        item { HorizontalDivider() }
        item {
            Text(
                text = stringResource(R.string.recent_activity),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 4.dp),
            )
        }
        items(stats.recentSubmissions, key = { it.id }) { submission ->
            SubmissionListItem(
                submission = submission,
                onClick = { onOpenSubmission(submission.id) },
            )
        }
    }
}

@Composable
private fun Rewards(rewards: List<RewardTotal>) {
    if (rewards.isEmpty()) {
        Text(
            text = "—",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
        )
        return
    }
    rewards.forEach { reward ->
        Text(
            text = "${reward.amount} ${reward.currency}".trim(),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

@Composable
private fun Metric(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
