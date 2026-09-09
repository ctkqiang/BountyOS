package com.bountyos.ui.detail

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.bountyos.R
import com.bountyos.domain.model.Activity
import com.bountyos.domain.model.Submission
import com.bountyos.ui.components.ProviderBadge
import com.bountyos.ui.components.SectionHeader
import com.bountyos.ui.components.StatusChip
import com.bountyos.ui.markdown.MarkdownText

/**
 * 报告详情屏幕。
 *
 * 展示平台返回的原始报告信息，漏洞正文以 Markdown 安全渲染（不执行
 * HTML/JavaScript），并提供「在平台中打开」跳转。
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
                        text = submission?.title ?: "",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
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
    activities: List<Activity>,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "#${submission.externalId}",
                    style = MaterialTheme.typography.labelLarge,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.width(8.dp))
                ProviderBadge(submission.provider)
            }
        }
        item {
            Row(
                modifier = Modifier.padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                StatusChip(submission.status, submission.providerStatus)
            }
        }
        item { HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp)) }

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

        submission.vulnerabilityInformation?.takeIf { it.isNotBlank() }?.let { content ->
            item {
                SectionHeader(stringResource(R.string.section_vulnerability))
            }
            item {
                MarkdownText(content = content)
            }
        }

        if (activities.isNotEmpty()) {
            item {
                SectionHeader(stringResource(R.string.section_activity))
            }
            items(activities, key = { it.id }) { activity ->
                ActivityItem(activity)
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
                        .padding(top = 24.dp),
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
            .padding(vertical = 6.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(120.dp),
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Composable
private fun ActivityItem(activity: Activity) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Text(
            text = activity.type,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontFamily = FontFamily.Monospace,
        )
        activity.message?.let { message ->
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 2.dp),
            )
        }
    }
}
