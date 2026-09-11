package com.bountyos.ui.ide

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.bountyos.R
import com.bountyos.domain.model.ScriptLanguage
import com.bountyos.ui.components.CodeHighlighter

/**
 * IDE 屏幕。
 *
 * 提供多语言代码编辑（语法高亮）、运行（本机解释器）与输出查看。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IdeScreen(navController: NavController) {
    val viewModel: IdeViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.ide_title)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
        ) {
            LanguageSelector(
                selected = state.language,
                onSelect = viewModel::onLanguageSelect,
            )
            CodeEditor(
                code = state.code,
                language = state.language,
                onCodeChange = viewModel::onCodeChange,
                modifier = Modifier.weight(1f),
            )
            ActionRow(
                isRunning = state.isRunning,
                onRun = viewModel::run,
                onReset = viewModel::reset,
            )
            OutputPanel(
                stdout = state.stdout,
                stderr = state.stderr,
                runError = state.runError,
                isRunning = state.isRunning,
                modifier = Modifier.weight(0.4f),
            )
        }
    }
}

@Composable
private fun LanguageSelector(
    selected: ScriptLanguage,
    onSelect: (ScriptLanguage) -> Unit,
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(ScriptLanguage.entries.size) { index ->
            val language = ScriptLanguage.entries[index]
            FilterChip(
                selected = selected == language,
                onClick = { onSelect(language) },
                label = { Text(language.displayName) },
            )
        }
    }
}

@Composable
private fun CodeEditor(
    code: String,
    language: ScriptLanguage,
    onCodeChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scheme = MaterialTheme.colorScheme
    val transformation = remember(language, scheme) {
        SyntaxHighlightTransformation(
            language = language,
            keyword = scheme.primary,
            string = Color(0xFF58A6FF),
            comment = scheme.onSurfaceVariant,
            number = scheme.secondary,
        )
    }
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        color = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.medium,
    ) {
        BasicTextField(
            value = code,
            onValueChange = onCodeChange,
            textStyle = TextStyle(
                fontFamily = FontFamily.Monospace,
                fontSize = 13.sp,
                color = scheme.onSurface,
            ),
            visualTransformation = transformation,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(12.dp),
            decorationBox = { innerTextField ->
                Box {
                    if (code.isEmpty()) {
                        Text(
                            text = stringResource(R.string.ide_placeholder),
                            style = TextStyle(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 13.sp,
                                color = scheme.onSurfaceVariant,
                            ),
                        )
                    }
                    innerTextField()
                }
            },
        )
    }
}

@Composable
private fun ActionRow(
    isRunning: Boolean,
    onRun: () -> Unit,
    onReset: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Button(
            onClick = onRun,
            enabled = !isRunning,
            modifier = Modifier.weight(1f),
        ) {
            if (isRunning) {
                CircularProgressIndicator(
                    modifier = Modifier.padding(vertical = 4.dp),
                    strokeWidth = 2.dp,
                )
            } else {
                Icon(Icons.Filled.PlayArrow, contentDescription = null)
                Text(
                    stringResource(R.string.ide_run),
                    modifier = Modifier.padding(start = 4.dp),
                )
            }
        }
        OutlinedButton(
            onClick = onReset,
            modifier = Modifier.weight(1f),
        ) {
            Icon(Icons.Filled.Refresh, contentDescription = null)
            Text(
                stringResource(R.string.ide_reset),
                modifier = Modifier.padding(start = 4.dp),
            )
        }
    }
}

@Composable
private fun OutputPanel(
    stdout: String,
    stderr: String,
    runError: String?,
    isRunning: Boolean,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        color = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.medium,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            Text(
                text = stringResource(R.string.ide_output),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            when {
                isRunning -> {
                    Text(
                        text = stringResource(R.string.ide_running),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                }
                runError != null -> {
                    Text(
                        text = runError,
                        style = MaterialTheme.typography.bodyMedium,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                }
                stdout.isBlank() && stderr.isBlank() -> {
                    Text(
                        text = stringResource(R.string.ide_empty_output),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                }
                else -> {
                    if (stdout.isNotBlank()) {
                        Text(
                            text = stdout,
                            style = MaterialTheme.typography.bodyMedium,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(top = 8.dp),
                        )
                    }
                    if (stderr.isNotBlank()) {
                        Text(
                            text = stderr,
                            style = MaterialTheme.typography.bodyMedium,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(top = 8.dp),
                        )
                    }
                }
            }
        }
    }
}

private class SyntaxHighlightTransformation(
    private val language: ScriptLanguage,
    private val keyword: Color,
    private val string: Color,
    private val comment: Color,
    private val number: Color,
) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText = TransformedText(
        text = CodeHighlighter.highlight(
            code = text.text,
            language = language,
            keyword = keyword,
            string = string,
            comment = comment,
            number = number,
        ),
        offsetMapping = OffsetMapping.Identity,
    )
}
