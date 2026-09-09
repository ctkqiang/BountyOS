package com.bountyos.ui.theme

import androidx.compose.ui.graphics.Color

/*
 * BountyOS 色板。
 *
 * 设计方向为「克制的暗色安全操作台」：近黑 charcoal 背景 + 单一
 * terminal green 强调色 + 语义化状态色。刻意避免渐变、霓虹与
 * 过度发光，保持专业安全工具的观感。
 */

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

/* 语义状态色 */
val ErrorRed = Color(0xFFF85149)
val WarningAmber = Color(0xFFD29922)
val InfoBlue = Color(0xFF58A6FF)
val SuccessGreen = Color(0xFF3FB950)

/* 文本 */
val TextPrimary = Color(0xFFE6EDF3)
val TextSecondary = Color(0xFF8B949E)
