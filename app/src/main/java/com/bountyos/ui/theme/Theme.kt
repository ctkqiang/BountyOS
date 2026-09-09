package com.bountyos.ui.theme

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.bountyos.domain.model.ThemeMode

/*
 * BountyOS 应用主题。
 *
 * 支持三种主题模式：跟随系统、强制浅色、强制深色。配色通过语义化
 * 颜色注入，不引入渐变。同时根据实际生效的深浅色设置系统状态栏与
 * 导航栏图标颜色，避免 edge-to-edge 下图标与背景同色而不可见。
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
    surfaceContainerLowest = Color(0xFF0B0F14),
    surfaceContainerLow = CharcoalSurface,
    surfaceContainer = CharcoalSurfaceElevated,
    surfaceContainerHigh = Color(0xFF272D37),
    surfaceContainerHighest = Color(0xFF2D333E),
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
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = LightSurface,
    surfaceContainer = Color(0xFFF0F3F6),
    surfaceContainerHigh = Color(0xFFE9EDF1),
    surfaceContainerHighest = Color(0xFFE1E6EB),
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

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = view.context.findActivity()?.window ?: return@SideEffect
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = BountyOsTypography,
        content = content,
    )
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
