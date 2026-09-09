package com.bountyos.ui.reports

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.bountyos.R
import com.bountyos.domain.model.Provider
import com.bountyos.domain.model.SubmissionStatus
import com.bountyos.ui.components.EmptyState
import com.bountyos.ui.components.SubmissionListItem
import com.bountyos.ui.components.submissionStatusLabel
import com.bountyos.ui.navigation.Route

/**
 * Reports 列表屏幕。
 *
 * 支持本地搜索与 provider/status 筛选。搜索/筛选仅作用于本地缓存
 * 数据，下拉刷新触发同步。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(navController: NavController) {
    val viewModel: ReportsViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = viewModel::refresh,
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            OutlinedTextField(
                value = state.query,
                onValueChange = viewModel::onQueryChange,
                label = { Text(stringResource(R.string.search_hint)) },
                leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                FilterChip(
                    selected = state.selectedProvider == null,
                    onClick = { viewModel.onProviderSelect(null) },
                    label = { Text(stringResource(R.string.filter_all)) },
                )
                Provider.entries.forEach { provider ->
                    FilterChip(
                        selected = state.selectedProvider == provider,
                        onClick = { viewModel.onProviderSelect(provider) },
                        label = { Text(providerLabel(provider)) },
                    )
                }
            }

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                item {
                    FilterChip(
                        selected = state.selectedStatus == null,
                        onClick = { viewModel.onStatusSelect(null) },
                        label = { Text(stringResource(R.string.filter_all)) },
                    )
                }
                items(SubmissionStatus.entries, key = { it.name }) { status ->
                    FilterChip(
                        selected = state.selectedStatus == status,
                        onClick = { viewModel.onStatusSelect(status) },
                        label = { Text(submissionStatusLabel(status)) },
                    )
                }
            }

            if (state.submissions.isEmpty()) {
                EmptyState(
                    title = stringResource(R.string.reports_empty_title),
                    description = stringResource(R.string.reports_empty_description),
                )
                return@Column
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp),
            ) {
                items(state.submissions, key = { it.id }) { submission ->
                    SubmissionListItem(
                        submission = submission,
                        onClick = { navController.navigate(Route.reportDetail(submission.id)) },
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                }
            }
        }
    }
}

@Composable
private fun providerLabel(provider: Provider): String = when (provider) {
    Provider.HACKERONE -> stringResource(R.string.hackerone)
    Provider.BUGCROWD -> stringResource(R.string.bugcrowd)
}
