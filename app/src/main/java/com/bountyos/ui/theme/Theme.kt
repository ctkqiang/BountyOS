package com.bountyos.ui.theme

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.bountyos.domain.model.ThemeMode

/*
 * BountyOS 应用主题 —— Apple Cupertino / Liquid Glass 风格。
 *
 * 支持三种主题模式：跟随系统、强制浅色、强制深色。配色为苹果系统
 * 蓝强调 + 苹果灰阶 + 半透明玻璃表面，表面叠加细描边营造玻璃层次。
 * 根据实际生效的深浅色设置系统状态栏/导航栏图标颜色，避免
 * edge-to-edge 下图标与背景同色不可见。
 */
private val DarkColorScheme = darkColorScheme(
    primary = TerminalGreen,
    onPrimary = OnTerminalGreen,
    primaryContainer = Color(0xFF0A2E5C),
    onPrimaryContainer = Color(0xFF8AB8FF),
    secondary = TextSecondary,
    onSecondary = CharcoalBackground,
    secondaryContainer = CharcoalSurfaceElevated,
    onSecondaryContainer = TextPrimary,
    background = CharcoalBackground,
    onBackground = TextPrimary,
    surface = CharcoalSurface,
    onSurface = TextPrimary,
    surfaceContainerLowest = Color(0xFF0A0A0C),
    surfaceContainerLow = Color(0xFF141416),
    surfaceContainer = CharcoalSurface,
    surfaceContainerHigh = CharcoalSurfaceElevated,
    surfaceContainerHighest = Color(0xFF2C2C2E),
    surfaceVariant = CharcoalSurfaceElevated,
    onSurfaceVariant = TextSecondary,
    outline = Color(0x1FFFFFFF),
    outlineVariant = Color(0x14FFFFFF),
    error = ErrorRed,
    onError = CharcoalBackground,
)

private val LightColorScheme = lightColorScheme(
    primary = LightGreen,
    onPrimary = OnLightGreen,
    primaryContainer = LightGreenContainer,
    onPrimaryContainer = OnLightGreenContainer,
    secondary = LightTextSecondary,
    onSecondary = LightBackground,
    secondaryContainer = LightSurfaceElevated,
    onSecondaryContainer = LightTextPrimary,
    background = LightBackground,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFFCFCFD),
    surfaceContainer = LightBackground,
    surfaceContainerHigh = LightSurfaceElevated,
    surfaceContainerHighest = Color(0xFFE8EAED),
    surfaceVariant = LightSurfaceElevated,
    onSurfaceVariant = LightTextSecondary,
    outline = Color(0x14000000),
    outlineVariant = Color(0x0A000000),
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
        shapes = BountyOsShapes,
        content = content,
    )
}

private val BountyOsShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(28.dp),
)

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
