package com.bountyos.ui.theme

import androidx.compose.ui.graphics.Color

/*
 * BountyOS 色板 —— Apple Cupertino 风格。
 *
 * 系统蓝强调 + 苹果灰阶 + 不透明表面，靠留白、圆角与发丝描边
 * 建立层次，不依赖半透明或彩色光晕。
 */

/* ===== 深色主题（Cupertino Dark） ===== */

/* 背景与表面 */
val CharcoalBackground = Color(0xFF000000)
val CharcoalSurface = Color(0xFF1C1C1E)
val CharcoalSurfaceElevated = Color(0xFF2C2C2E)

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

/* 背景与表面 */
val LightBackground = Color(0xFFF2F2F7)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceElevated = Color(0xFFFFFFFF)

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
