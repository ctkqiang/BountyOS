package com.bountyos.ui.activity

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bountyos.R
import com.bountyos.domain.model.Activity
import com.bountyos.ui.components.EmptyState
import com.bountyos.ui.components.ProviderBadge

/**
 * 统一活动时间线屏幕。
 */
@Composable
fun ActivityScreen() {
    val viewModel: ActivityViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    if (state.activities.isEmpty()) {
        EmptyState(
            title = stringResource(R.string.activity_empty_title),
            description = stringResource(R.string.activity_empty_description),
        )
        return
    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(state.activities, key = { it.id }) { activity ->
            ActivityItem(activity)
            HorizontalDivider()
        }
    }
}

@Composable
private fun ActivityItem(activity: Activity) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        Row {
            ProviderBadge(activity.provider)
            Text(
                text = "#${activity.submissionExternalId}",
                style = MaterialTheme.typography.labelMedium,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Text(
            text = activity.message ?: activity.type,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}
