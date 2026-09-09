package com.bountyos.ui.theme

import androidx.compose.ui.graphics.Color

/*
 * BountyOS 色板。
 *
 * 设计方向：字节跳动式的克制深色操作台。接近纯黑的冷调背景 +
 * 单一锐利 terminal green 强调 + 精确灰阶文字层级。避免渐变与
 * 过度发光，靠层次与留白建立高级感。
 */

/* ===== 深色主题（默认主打） ===== */

/* 背景与表面 */
val CharcoalBackground = Color(0xFF0B0C0E)
val CharcoalSurface = Color(0xFF141619)
val CharcoalSurfaceElevated = Color(0xFF1C1F24)

/* 强调色：锐利 terminal green */
val TerminalGreen = Color(0xFF3FB950)
val OnTerminalGreen = Color(0xFF0B0C0E)

/* 深色语义状态色 */
val ErrorRed = Color(0xFFF85149)
val WarningAmber = Color(0xFFD29922)
val InfoBlue = Color(0xFF58A6FF)
val SuccessGreen = Color(0xFF3FB950)

/* 深色文本 */
val TextPrimary = Color(0xFFF2F4F7)
val TextSecondary = Color(0xFF9AA3AE)
val TextTertiary = Color(0xFF6E7681)

/* ===== 浅色主题 ===== */

/* 背景与表面 */
val LightBackground = Color(0xFFF7F8FA)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceElevated = Color(0xFFEFF1F4)

/* 强调色：浅色背景上更深的 green */
val LightGreen = Color(0xFF1F883D)
val OnLightGreen = Color(0xFFFFFFFF)
val LightGreenContainer = Color(0xFFDAFBE1)
val OnLightGreenContainer = Color(0xFF116329)

/* 浅色语义状态色 */
val LightErrorRed = Color(0xFFCF222E)
val LightWarningAmber = Color(0xFF9A6700)
val LightInfoBlue = Color(0xFF0969DA)
val LightSuccessGreen = Color(0xFF1A7F37)

/* 浅色文本 */
val LightTextPrimary = Color(0xFF1F2328)
val LightTextSecondary = Color(0xFF57606A)
val LightTextTertiary = Color(0xFF6E7781)
