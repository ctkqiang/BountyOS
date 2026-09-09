package com.bountyos.ui.components

import androidx.compose.ui.hapticfeedback.HapticFeedbackType

/**
 * 应用内统一的触觉反馈分级。
 *
 * 两级强度均兼容 minSdk 26（API 26），无需按版本分支：
 * [Tap] 为标准点击震感，用于列表项、筛选 chip、底部导航、FAB 等
 * 用户需要明确「点到了」的交互；[Tick] 为更轻的即时 tick，用于主题切换
 * 等已有视觉反馈、仅需轻微触感的连续交互。
 */
object Haptics {
    /** 标准点击反馈。 */
    val Tap = HapticFeedbackType.LongPress

    /** 轻量 tick，用于微调 / 即时反馈。 */
    val Tick = HapticFeedbackType.TextHandleMove
}
