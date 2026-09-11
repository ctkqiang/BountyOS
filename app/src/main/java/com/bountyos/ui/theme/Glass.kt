package com.bountyos.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

/**
 * 发丝描边 modifier：1dp 细线，用于卡片与表面边界。
 * 颜色取自主题 outline（低透明度），保持 Cupertino 的利落层次，
 * 不喧宾夺主。
 */
fun Modifier.glassBorder(shape: Shape = RoundedCornerShape(16.dp)): Modifier = composed {
    border(BorderStroke(1.dp, MaterialTheme.colorScheme.outline), shape)
}

/**
 * 应用背景：极淡的垂直渐变（顶部略亮），为不透明表面提供层次。
 * 不使用彩色光晕，避免与内容抢注意力。
 */
@Composable
fun GlassBackground(modifier: Modifier = Modifier) {
    val scheme = MaterialTheme.colorScheme
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        scheme.surfaceContainerLow,
                        scheme.background,
                    ),
                ),
            ),
    )
}
