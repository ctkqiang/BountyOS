package com.bountyos.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

/**
 * 玻璃描边 modifier：细描边 + 圆角，营造玻璃边缘高光。
 */
fun Modifier.glassBorder(shape: Shape = RoundedCornerShape(16.dp)): Modifier = composed {
    this.border(
        BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        shape,
    )
}

/**
 * 氛围光晕背景。
 *
 * 在根布局铺一层柔和的系统蓝/灰光晕，半透明玻璃表面透出这些光晕，
 * 从而呈现 Liquid Glass 的层次与景深。纯装饰，不含交互。
 */
@Composable
fun GlassBackground(modifier: Modifier = Modifier) {
    val scheme = MaterialTheme.colorScheme
    Box(modifier = modifier.fillMaxSize()) {
        // 顶部系统蓝光晕
        Box(
            modifier = Modifier
                .size(360.dp)
                .align(Alignment.TopEnd)
                .offset(x = 80.dp, y = (-120).dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            scheme.primary.copy(alpha = 0.28f),
                            Color.Transparent,
                        ),
                    )
                ),
        )
        // 中部灰调光晕
        Box(
            modifier = Modifier
                .size(300.dp)
                .align(Alignment.CenterStart)
                .offset(x = (-80).dp, y = 60.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            scheme.secondary.copy(alpha = 0.16f),
                            Color.Transparent,
                        ),
                    )
                ),
        )
        // 底部系统蓝光晕
        Box(
            modifier = Modifier
                .size(320.dp)
                .align(Alignment.BottomStart)
                .offset(x = (-60).dp, y = 100.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            scheme.primary.copy(alpha = 0.18f),
                            Color.Transparent,
                        ),
                    )
                ),
        )
    }
}
