package com.bountyos.ui.mitre

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.bountyos.R
import com.bountyos.domain.model.MitreTechnique
import com.bountyos.ui.components.Haptics
import com.bountyos.ui.theme.glassBorder

/**
 * MITRE ATT&CK 矩阵屏幕。
 *
 * 按战术分组展示全部 Enterprise 技术（含子技术），支持按 ID 或名称
 * 过滤。数据不在后台静默拉取：无缓存时由用户显式点击下载。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MitreScreen(navController: NavController) {
    val viewModel: MitreViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val haptics = LocalHapticFeedback.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.mitre_title)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            haptics.performHapticFeedback(Haptics.Tap)
                            viewModel.download()
                        },
                        enabled = !state.isLoading,
                    ) {
                        Icon(
                            Icons.Filled.Refresh,
                            contentDescription = stringResource(R.string.mitre_refresh),
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
        ) {
            when {
                state.isLoading -> LoadingState()
                state.hasError -> MessageState(
                    message = stringResource(R.string.mitre_error),
                    actionLabel = stringResource(R.string.mitre_retry),
                    onAction = viewModel::download,
                )

                state.needsDownload -> MessageState(
                    message = stringResource(R.string.mitre_idle_hint),
                    actionLabel = stringResource(R.string.mitre_download),
                    onAction = viewModel::download,
                )

                else -> MatrixContent(
                    state = state,
                    onQueryChange = viewModel::onQueryChange,
                )
            }
        }
    }
}

@Composable
private fun MatrixContent(
    state: MitreUiState,
    onQueryChange: (String) -> Unit,
) {
    val matrix = state.matrix ?: return

    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = state.query,
            onValueChange = onQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            placeholder = { Text(stringResource(R.string.mitre_search_hint)) },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
            singleLine = true,
            shape = MaterialTheme.shapes.medium,
        )
        Text(
            text = stringResource(R.string.mitre_version, matrix.version),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
        )

        if (state.groups.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(R.string.mitre_empty),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, top = 4.dp, end = 16.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                state.groups.forEach { group ->
                    item(key = "tactic-${group.tactic.id}") {
                        TacticHeader(group)
                    }
                    items(group.techniques, key = { "${group.tactic.id}-${it.id}" }) { technique ->
                        TechniqueRow(technique)
                    }
                }
            }
        }
    }
}

@Composable
private fun TacticHeader(group: MitreTacticGroup) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp, bottom = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = group.tactic.name,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = group.tactic.id,
            style = MaterialTheme.typography.labelMedium,
            fontFamily = FontFamily.Monospace,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
    Text(
        text = stringResource(R.string.mitre_technique_count, group.techniques.size),
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
private fun TechniqueRow(technique: MitreTechnique) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = if (technique.isSubTechnique) 20.dp else 0.dp)
            .glassBorder(MaterialTheme.shapes.medium),
        color = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.medium,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = technique.id,
                    style = MaterialTheme.typography.labelLarge,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.primary,
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = technique.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
            }
            if (technique.platforms.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = technique.platforms.joinToString(separator = " · "),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun MessageState(
    message: String,
    actionLabel: String,
    onAction: () -> Unit,
) {
    val haptics = LocalHapticFeedback.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                haptics.performHapticFeedback(Haptics.Tap)
                onAction()
            },
        ) {
            Text(actionLabel)
        }
    }
}
