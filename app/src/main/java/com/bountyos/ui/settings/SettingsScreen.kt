package com.bountyos.ui.settings

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bountyos.R
import com.bountyos.domain.model.Provider
import com.bountyos.domain.model.ThemeMode
import com.bountyos.ui.components.Haptics
import com.bountyos.ui.theme.glassBorder

/**
 * Settings（连接管理、主题偏好）屏幕。
 *
 * 作为底部导航的「设置」tab 直接展示。内容以卡片分区组织：连接、
 * 主题、关于。凭证仅在此输入并经 Keystore 加密保存，界面不回显 token。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    val viewModel: SettingsViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val haptics = LocalHapticFeedback.current
    var connectTarget by remember { mutableStateOf<Provider?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Text(
                text = stringResource(R.string.settings),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
            )
        }

        item {
            SettingsCard(title = stringResource(R.string.settings_connections)) {
                Provider.entries.forEach { provider ->
                    val integration = state.integrations.firstOrNull { it.provider == provider }
                    val connected = integration?.connected == true
                    ListItem(
                        headlineContent = { Text(providerName(provider)) },
                        supportingContent = {
                            Text(
                                if (connected) stringResource(R.string.connection_connected)
                                else stringResource(R.string.connection_not_connected)
                            )
                        },
                        trailingContent = {
                            if (connected) {
                                TextButton(onClick = {
                                    haptics.performHapticFeedback(Haptics.Tap)
                                    viewModel.disconnect(provider)
                                }) {
                                    Text(stringResource(R.string.disconnect))
                                }
                            } else {
                                TextButton(onClick = {
                                    haptics.performHapticFeedback(Haptics.Tap)
                                    connectTarget = provider
                                }) {
                                    Text(stringResource(R.string.connect))
                                }
                            }
                        },
                    )
                }
            }
        }

        item {
            SettingsCard(title = stringResource(R.string.settings_theme)) {
                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                ) {
                    ThemeMode.entries.forEachIndexed { index, mode ->
                        SegmentedButton(
                            selected = state.themeMode == mode,
                            onClick = {
                                haptics.performHapticFeedback(Haptics.Tick)
                                viewModel.setThemeMode(mode)
                            },
                            shape = SegmentedButtonDefaults.itemShape(index = index, count = ThemeMode.entries.size),
                        ) {
                            Text(themeModeLabel(mode))
                        }
                    }
                }
            }
        }

        item {
            SettingsCard(title = stringResource(R.string.about)) {
                ListItem(
                    headlineContent = { Text(stringResource(R.string.about_author)) },
                    supportingContent = { Text(stringResource(R.string.author_name)) },
                    modifier = Modifier.clickable {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(AUTHOR_URL)))
                    },
                )
                ListItem(
                    headlineContent = { Text(stringResource(R.string.about_source)) },
                    supportingContent = {
                        Text(
                            text = SOURCE_URL,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    },
                    modifier = Modifier.clickable {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(SOURCE_URL)))
                    },
                )
                Text(
                    text = stringResource(R.string.about_open_source),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                )
            }
        }
    }

    connectTarget?.let { provider ->
        ConnectDialog(
            provider = provider,
            onDismiss = { connectTarget = null },
            onConfirm = { username, token ->
                viewModel.connect(provider, username, token) { result ->
                    connectTarget = null
                    haptics.performHapticFeedback(Haptics.Tap)
                    val message = if (result.isSuccess) {
                        context.getString(R.string.connection_success)
                    } else {
                        context.getString(R.string.connection_failed)
                    }
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                }
            },
        )
    }
}

/** 分区卡片：带标题栏的圆角容器。 */
@Composable
private fun SettingsCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .glassBorder(MaterialTheme.shapes.large),
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = MaterialTheme.shapes.large,
    ) {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )
            content()
        }
    }
}

private const val SOURCE_URL = "https://github.com/ctkqiang/BountyOS"
private const val AUTHOR_URL = "https://www.ctkqiang.xin"

private fun providerName(provider: Provider): String = when (provider) {
    Provider.HACKERONE -> "HackerOne"
    Provider.BUGCROWD -> "Bugcrowd"
    Provider.INTIGRITI -> "Intigriti"
    Provider.YESWEHACK -> "YesWeHack"
}

@Composable
private fun themeModeLabel(mode: ThemeMode): String = when (mode) {
    ThemeMode.SYSTEM -> stringResource(R.string.theme_system)
    ThemeMode.LIGHT -> stringResource(R.string.theme_light)
    ThemeMode.DARK -> stringResource(R.string.theme_dark)
}

@Composable
private fun ConnectDialog(
    provider: Provider,
    onDismiss: () -> Unit,
    onConfirm: (username: String?, token: String) -> Unit,
) {
    var username by remember { mutableStateOf("") }
    var token by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.connect_to, providerName(provider))) },
        text = {
            Column {
                if (provider == Provider.HACKERONE) {
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text(stringResource(R.string.username_hint)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                OutlinedTextField(
                    value = token,
                    onValueChange = { token = it },
                    label = { Text(stringResource(R.string.token_hint)) },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(
                        username.takeIf { provider == Provider.HACKERONE },
                        token.trim(),
                    )
                },
                enabled = token.isNotBlank(),
            ) {
                Text(stringResource(R.string.connect))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        },
    )
}
