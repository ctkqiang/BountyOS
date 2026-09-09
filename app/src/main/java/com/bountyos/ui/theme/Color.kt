package com.bountyos.ui.theme

import androidx.compose.ui.graphics.Color

/*
 * BountyOS 色板。
 *
 * 设计方向为「克制的安全操作台」：charcoal 背景 + 单一 terminal green
 * 强调色 + 语义化状态色。刻意避免渐变、霓虹与过度发光。
 *
 * 提供深色与浅色两套色值，浅色沿用 GitHub 浅色风格的克制观感，
 * 保证文本对比度满足可读性。
 */

/* ===== 深色主题 ===== */

/* 背景与表面 */
val CharcoalBackground = Color(0xFF0D1117)
val CharcoalSurface = Color(0xFF161B22)
val CharcoalSurfaceElevated = Color(0xFF21262D)

/* 强调色：克制的 terminal green */
val TerminalGreen = Color(0xFF3FB950)
val OnTerminalGreen = Color(0xFF0D1117)

/* 中性色 */
val CoolNeutral = Color(0xFF8B949E)
val MutedNeutral = Color(0xFF6E7681)

/* 深色语义状态色 */
val ErrorRed = Color(0xFFF85149)
val WarningAmber = Color(0xFFD29922)
val InfoBlue = Color(0xFF58A6FF)
val SuccessGreen = Color(0xFF3FB950)

/* 深色文本 */
val TextPrimary = Color(0xFFE6EDF3)
val TextSecondary = Color(0xFF8B949E)

/* ===== 浅色主题 ===== */

/* 背景与表面 */
val LightBackground = Color(0xFFFFFFFF)
val LightSurface = Color(0xFFF6F8FA)
val LightSurfaceElevated = Color(0xFFEFF2F5)

/* 强调色：浅色背景上更深的 green，保证对比度 */
val LightGreen = Color(0xFF1F883D)
val OnLightGreen = Color(0xFFFFFFFF)
val LightGreenContainer = Color(0xFFDAFBE1)
val OnLightGreenContainer = Color(0xFF116329)

/* 浅色中性色 */
val LightNeutral = Color(0xFF57606A)
val LightMutedNeutral = Color(0xFF6E7781)

/* 浅色语义状态色（对比度满足 WCAG AA） */
val LightErrorRed = Color(0xFFCF222E)
val LightWarningAmber = Color(0xFF9A6700)
val LightInfoBlue = Color(0xFF0969DA)
val LightSuccessGreen = Color(0xFF1A7F37)

/* 浅色文本 */
val LightTextPrimary = Color(0xFF1F2328)
val LightTextSecondary = Color(0xFF57606A)
