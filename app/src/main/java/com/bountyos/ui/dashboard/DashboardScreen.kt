package com.bountyos.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.bountyos.R
import com.bountyos.domain.aggregation.DashboardStats
import com.bountyos.domain.aggregation.RewardTotal
import com.bountyos.ui.components.EmptyState
import com.bountyos.ui.components.SectionHeader
import com.bountyos.ui.components.SubmissionListItem
import com.bountyos.ui.hai.HaiChatPanel
import com.bountyos.ui.navigation.BottomDestination
import com.bountyos.ui.navigation.Route

/**
 * Dashboard 主屏幕。
 *
 * 顶部突出总赏金，下方以指标网格展示提交统计与最近提交。未连接任何
 * 平台时显示 onboarding 空状态。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavController) {
    val viewModel: DashboardViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showChat by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            state.isLoading -> LoadingContent()
            !state.hasIntegrations -> {
                EmptyState(
                    title = stringResource(R.string.onboarding_title),
                    description = stringResource(R.string.onboarding_description),
                    action = {
                        Button(onClick = { navController.navigate(BottomDestination.MORE.route) }) {
                            Text(stringResource(R.string.connect_platform))
                        }
                    },
                )
            }
            else -> {
                val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
                PullToRefreshBox(
                    isRefreshing = isRefreshing,
                    onRefresh = viewModel::refresh,
                ) {
                    DashboardContent(
                        stats = state.stats,
                        onOpenSubmission = { id -> navController.navigate(Route.reportDetail(id)) },
                    )
                }
            }
        }

        if (state.hasHackerOne && !showChat) {
            FloatingActionButton(
                onClick = { showChat = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
            ) {
                Icon(
                    Icons.Filled.AutoAwesome,
                    contentDescription = stringResource(R.string.hai_title),
                )
            }
        }

        if (showChat) {
            HaiChatPanel(
                onClose = { showChat = false },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .fillMaxHeight(0.75f)
                    .padding(12.dp),
            )
        }
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
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            SummaryCard(stats)
        }
        item {
            SectionHeader(
                title = stringResource(R.string.recent_activity),
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp),
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
private fun SummaryCard(stats: DashboardStats) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.large,
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = stringResource(R.string.total_bounties),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Rewards(stats.totalRewards)
            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                Metric(
                    label = stringResource(R.string.submissions),
                    value = stats.submissionCount.toString(),
                    modifier = Modifier.weight(1f),
                )
                Metric(
                    label = stringResource(R.string.attention),
                    value = stats.attentionCount.toString(),
                    modifier = Modifier.weight(1f),
                )
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                Metric(
                    label = stringResource(R.string.hackerone),
                    value = stats.hackerOneCount.toString(),
                    modifier = Modifier.weight(1f),
                )
                Metric(
                    label = stringResource(R.string.bugcrowd),
                    value = stats.bugcrowdCount.toString(),
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun Rewards(rewards: List<RewardTotal>) {
    if (rewards.isEmpty()) {
        Text(
            text = "$0.00",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary,
        )
        return
    }
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        rewards.forEach { reward ->
            Text(
                text = "${reward.amount} ${reward.currency}".trim(),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
private fun Metric(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(vertical = 8.dp),
        horizontalAlignment = Alignment.Start,
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
