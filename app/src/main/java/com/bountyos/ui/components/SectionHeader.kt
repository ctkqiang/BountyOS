package com.bountyos.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * 分区标题。
 *
 * 使用主题色的 label 样式，作为内容分区的视觉分隔，在 Dashboard、
 * 报告详情等屏幕统一复用。
 */
@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier.padding(top = 24.dp, bottom = 8.dp),
) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier,
    )
}
