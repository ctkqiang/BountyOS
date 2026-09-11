package com.bountyos.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.bountyos.R
import com.bountyos.domain.aggregation.DashboardStats
import com.bountyos.domain.aggregation.RewardTotal
import com.bountyos.domain.model.Program
import com.bountyos.domain.model.Provider
import com.bountyos.ui.components.EmptyState
import com.bountyos.ui.components.Haptics
import com.bountyos.ui.components.ProviderBadge
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
    val haptics = LocalHapticFeedback.current
    var showChat by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.nav_dashboard)) },
                actions = {
                    IconButton(onClick = {
                        haptics.performHapticFeedback(Haptics.Tap)
                        navController.navigate(Route.IDE)
                    }) {
                        Icon(
                            Icons.Outlined.Code,
                            contentDescription = stringResource(R.string.ide_title),
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
        when {
            state.isLoading -> LoadingContent()
            !state.hasIntegrations -> {
                EmptyState(
                    title = stringResource(R.string.onboarding_title),
                    description = stringResource(R.string.onboarding_description),
                    action = {
                        Button(onClick = {
                            haptics.performHapticFeedback(Haptics.Tap)
                            navController.navigate(BottomDestination.MORE.route)
                        }) {
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
                        followingPrograms = state.followingPrograms,
                        onOpenSubmission = { id -> navController.navigate(Route.reportDetail(id)) },
                    )
                }
            }
        }

        if (state.hasHackerOne && !showChat) {
            FloatingActionButton(
                onClick = {
                    haptics.performHapticFeedback(Haptics.Tap)
                    showChat = true
                },
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
    followingPrograms: List<Program>,
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
        if (followingPrograms.isNotEmpty()) {
            item {
                SectionHeader(
                    title = stringResource(R.string.dashboard_following),
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp),
                )
            }
            items(followingPrograms, key = { it.id }) { program ->
                FollowingProgramRow(program)
            }
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
private fun FollowingProgramRow(program: Program) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.medium,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ProviderBadge(program.provider)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = program.name ?: program.handle,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (program.name != null && program.handle != program.name) {
                    Text(
                        text = program.handle,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
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
            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
            Text(
                text = stringResource(R.string.dashboard_platforms),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 4.dp),
            )
            Provider.entries.forEach { provider ->
                PlatformRow(
                    provider = provider,
                    count = stats.submissionsByProvider[provider] ?: 0,
                )
            }
        }
    }
}

@Composable
private fun PlatformRow(provider: Provider, count: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ProviderBadge(provider)
        Spacer(Modifier.width(10.dp))
        Text(
            text = providerName(provider),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun providerName(provider: Provider): String = when (provider) {
    Provider.HACKERONE -> stringResource(R.string.hackerone)
    Provider.BUGCROWD -> stringResource(R.string.bugcrowd)
    Provider.INTIGRITI -> stringResource(R.string.intigriti)
    Provider.YESWEHACK -> stringResource(R.string.yeswehack)
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
