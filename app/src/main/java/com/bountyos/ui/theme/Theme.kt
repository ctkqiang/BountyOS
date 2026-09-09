package com.bountyos.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

/*
 * BountyOS 应用主题。
 *
 * BountyOS 是暗色优先的安全操作台，因此始终使用暗色配色方案，
 * 不随系统亮色主题切换。配色仅通过语义化颜色注入，不引入渐变。
 */
private val DarkColorScheme = darkColorScheme(
    primary = TerminalGreen,
    onPrimary = OnTerminalGreen,
    primaryContainer = CharcoalSurfaceElevated,
    onPrimaryContainer = TerminalGreen,
    secondary = CoolNeutral,
    onSecondary = CharcoalBackground,
    secondaryContainer = CharcoalSurfaceElevated,
    onSecondaryContainer = TextPrimary,
    background = CharcoalBackground,
    onBackground = TextPrimary,
    surface = CharcoalSurface,
    onSurface = TextPrimary,
    surfaceVariant = CharcoalSurfaceElevated,
    onSurfaceVariant = TextSecondary,
    error = ErrorRed,
    onError = CharcoalBackground,
)

@Composable
fun BountyOsTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = BountyOsTypography,
        content = content,
    )
}
