package com.bountyos.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import com.bountyos.domain.model.ThemeMode

/*
 * BountyOS 应用主题。
 *
 * 支持三种主题模式：跟随系统、强制浅色、强制深色。配色通过语义化
 * 颜色注入，不引入渐变。
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

private val LightColorScheme = lightColorScheme(
    primary = LightGreen,
    onPrimary = OnLightGreen,
    primaryContainer = LightGreenContainer,
    onPrimaryContainer = OnLightGreenContainer,
    secondary = LightNeutral,
    onSecondary = LightBackground,
    secondaryContainer = LightSurfaceElevated,
    onSecondaryContainer = LightTextPrimary,
    background = LightBackground,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceElevated,
    onSurfaceVariant = LightTextSecondary,
    error = LightErrorRed,
    onError = OnLightGreen,
)

@Composable
fun BountyOsTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit,
) {
    val darkTheme = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = BountyOsTypography,
        content = content,
    )
}
