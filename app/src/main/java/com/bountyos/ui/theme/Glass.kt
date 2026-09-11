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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeChild

/**
 * 发丝描边 modifier：1dp 细线，用于卡片与表面边界。
 * 颜色取自主题 outline（低透明度），保持 Cupertino 的利落层次。
 */
fun Modifier.glassBorder(shape: Shape = RoundedCornerShape(16.dp)): Modifier = composed {
    border(BorderStroke(1.dp, MaterialTheme.colorScheme.outline), shape)
}

/**
 * 玻璃效果 modifier：基于 Haze 的真实 backdrop 模糊。
 *
 * 被标记的节点会成为「玻璃」，模糊其后方滚动的内容。调用方必须在
 * 内容侧用 [dev.chrisbanes.haze.haze] 标记模糊来源，否则没有可模糊的输入。
 *
 * @param state 与来源节点共享的 [HazeState]。
 * @param shape 玻璃表面的形状（圆角）。
 * @param blurRadius 模糊半径，越大越朦胧。
 */
fun Modifier.glassEffect(
    state: HazeState,
    shape: Shape = RoundedCornerShape(16.dp),
    blurRadius: Dp = 28.dp,
): Modifier = composed {
    val surface = MaterialTheme.colorScheme.surface
    clip(shape).hazeChild(
        state = state,
        style = HazeStyle(
            backgroundColor = surface,
            tints = listOf(HazeTint(surface.copy(alpha = 0.55f))),
            blurRadius = blurRadius,
            noiseFactor = 0f,
        ),
    )
}

/**
 * 应用背景：极淡的垂直渐变，为玻璃表面提供可透出的底衬。
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
