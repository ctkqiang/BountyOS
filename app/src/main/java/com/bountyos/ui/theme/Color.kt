package com.bountyos.ui.theme

import androidx.compose.ui.graphics.Color

/*
 * BountyOS 色板 —— Apple Cupertino / Liquid Glass 风格。
 *
 * 系统蓝强调 + 苹果灰阶 + 半透明玻璃表面。表面使用带 alpha 的
 * 玻璃色，叠加细描边（outline）营造层次与景深。避免渐变堆砌，
 * 靠留白、圆角与半透明建立高级感。
 */

/* ===== 深色主题（Cupertino Dark） ===== */

/* 背景与表面（半透明玻璃） */
val CharcoalBackground = Color(0xFF000000)
val CharcoalSurface = Color(0xB31C1C1E)
val CharcoalSurfaceElevated = Color(0xD92C2C2E)

/* 强调色：系统蓝 */
val TerminalGreen = Color(0xFF0A84FF)
val OnTerminalGreen = Color(0xFFFFFFFF)

/* 深色语义状态色（iOS Dark） */
val ErrorRed = Color(0xFFFF453A)
val WarningAmber = Color(0xFFFF9F0A)
val InfoBlue = Color(0xFF64D2FF)
val SuccessGreen = Color(0xFF30D158)

/* 深色文本 */
val TextPrimary = Color(0xFFFFFFFF)
val TextSecondary = Color(0xFF98989E)
val TextTertiary = Color(0xFF5A5A5E)

/* ===== 浅色主题（Cupertino Light） ===== */

/* 背景与表面（半透明玻璃） */
val LightBackground = Color(0xFFF2F2F7)
val LightSurface = Color(0xB8FFFFFF)
val LightSurfaceElevated = Color(0xE6FFFFFF)

/* 强调色：系统蓝 */
val LightGreen = Color(0xFF007AFF)
val OnLightGreen = Color(0xFFFFFFFF)
val LightGreenContainer = Color(0xFFE5F0FF)
val OnLightGreenContainer = Color(0xFF0055CC)

/* 浅色语义状态色（iOS Light） */
val LightErrorRed = Color(0xFFFF3B30)
val LightWarningAmber = Color(0xFFFF9500)
val LightInfoBlue = Color(0xFF007AFF)
val LightSuccessGreen = Color(0xFF34C759)

/* 浅色文本 */
val LightTextPrimary = Color(0xFF000000)
val LightTextSecondary = Color(0xFF8E8E93)
val LightTextTertiary = Color(0xFFC7C7CC)
