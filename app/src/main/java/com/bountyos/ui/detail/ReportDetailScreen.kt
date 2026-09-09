package com.bountyos.ui.detail

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.bountyos.R
import com.bountyos.domain.model.Submission
import com.bountyos.ui.components.ProviderBadge
import com.bountyos.ui.components.StatusChip

/**
 * 报告详情屏幕。
 *
 * 展示平台返回的原始报告信息，并提供「在平台中打开」跳转。报告内容
 * 以纯文本展示（不执行 Markdown / HTML），始终视为不可信内容。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportDetailScreen(navController: NavController) {
    val viewModel: ReportDetailViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val submission = state.submission

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = submission?.let { "#${it.externalId}" } ?: "",
                        fontFamily = FontFamily.Monospace,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
            )
        },
    ) { padding ->
        if (submission == null) return@Scaffold
        DetailContent(
            submission = submission,
            activities = state.activities,
            modifier = Modifier.padding(padding),
        )
    }
}

@Composable
private fun DetailContent(
    submission: Submission,
    activities: List<com.bountyos.domain.model.Activity>,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
    ) {
        item {
            Text(submission.title, style = MaterialTheme.typography.headlineMedium)
        }
        item {
            Row(modifier = Modifier.padding(vertical = 8.dp)) {
                ProviderBadge(submission.provider)
                StatusChip(submission.status, submission.providerStatus)
            }
        }
        item { HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp)) }

        item {
            DetailRow(stringResource(R.string.field_program), submission.programName)
            DetailRow(stringResource(R.string.field_severity), submission.providerSeverity)
            DetailRow(stringResource(R.string.field_weakness), submission.weakness?.name)
            DetailRow(stringResource(R.string.field_cwe), submission.weakness?.cweId)
            DetailRow(
                stringResource(R.string.field_reward),
                submission.reward?.let { "${it.amount} ${it.currency.orEmpty()}".trim() },
            )
        }
        item { HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp)) }

        submission.vulnerabilityInformation?.let { content ->
            item {
                Text(stringResource(R.string.section_vulnerability), style = MaterialTheme.typography.titleMedium)
            }
            item {
                Text(
                    text = content,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
            item { HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp)) }
        }

        item {
            Text(stringResource(R.string.section_activity), style = MaterialTheme.typography.titleMedium)
        }
        items(activities, key = { it.id }) { activity ->
            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                Text(
                    text = activity.message ?: activity.type,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }

        submission.canonicalUrl?.let { url ->
            item {
                Button(
                    onClick = {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                ) {
                    Text(
                        stringResource(
                            R.string.open_in_provider,
                            if (submission.provider.name == "HACKERONE") "HackerOne" else "Bugcrowd",
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String?) {
    if (value.isNullOrBlank()) return
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(120.dp),
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}
