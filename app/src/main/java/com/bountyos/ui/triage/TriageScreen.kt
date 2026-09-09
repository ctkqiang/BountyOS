package com.bountyos.ui.triage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.bountyos.ui.components.EmptyState
import com.bountyos.ui.components.SubmissionListItem
import com.bountyos.ui.navigation.Route

/**
 * Triage（Attention Inbox）屏幕。
 *
 * 只读展示需要关注的提交。用户通过「在平台中打开」跳转原平台处理，
 * BountyOS 不做任何写入。支持下拉刷新触发同步。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TriageScreen(navController: NavController) {
    val viewModel: TriageViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = viewModel::refresh,
    ) {
        if (state.attentionItems.isEmpty()) {
            EmptyState(
                title = stringResource(R.string.triage_empty_title),
                description = stringResource(R.string.triage_empty_description),
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(state.attentionItems, key = { it.id }) { submission ->
                    SubmissionListItem(
                        submission = submission,
                        onClick = { navController.navigate(Route.reportDetail(submission.id)) },
                    )
                }
            }
        }
    }
}
