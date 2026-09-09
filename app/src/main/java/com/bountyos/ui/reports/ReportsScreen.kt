package com.bountyos.ui.reports

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import com.bountyos.ui.components.EmptyState
import com.bountyos.ui.components.SubmissionListItem
import com.bountyos.ui.navigation.Route

/**
 * Reports 列表屏幕。
 *
 * 支持本地搜索与 provider 筛选。搜索/筛选仅作用于本地缓存数据。
 */
@Composable
fun ReportsScreen(navController: NavController) {
    val viewModel: ReportsViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = state.query,
            onValueChange = viewModel::onQueryChange,
            label = { Text(stringResource(R.string.search_hint)) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
        )

        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
        ) {
            FilterChip(
                selected = state.selectedProvider == null,
                onClick = { viewModel.onProviderSelect(null) },
                label = { Text(stringResource(R.string.filter_all)) },
                modifier = Modifier.padding(end = 8.dp),
            )
            Provider.entries.forEach { provider ->
                FilterChip(
                    selected = state.selectedProvider == provider,
                    onClick = { viewModel.onProviderSelect(provider) },
                    label = { Text(providerLabel(provider)) },
                    modifier = Modifier.padding(end = 8.dp),
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
            }
        }
    }
}

@Composable
private fun providerLabel(provider: Provider): String = when (provider) {
    Provider.HACKERONE -> stringResource(R.string.hackerone)
    Provider.BUGCROWD -> stringResource(R.string.bugcrowd)
}
