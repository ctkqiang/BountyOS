package com.bountyos.ui.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bountyos.ui.components.Haptics

/** tab bar 内容高度，不含系统导航栏 inset。 */
private val BarHeight = 52.dp

/**
 * 自绘的 iOS 风格底部 tab bar。
 *
 * 不使用 Material3 的 NavigationBar：没有平移指示条、没有水波纹，
 * 选中态以 tint 变色 + 轻微缩放 + 字重变化表达；图标采用选中实心、
 * 未选中描边两套。底部的系统导航栏 inset 由自身消化，玻璃背景由调用
 * 方通过 modifier 注入（见 glassEffect）。
 */
@Composable
fun BountyTabBar(
    destinations: List<BottomDestination>,
    selectedRoute: String?,
    onSelect: (BottomDestination) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .height(BarHeight)
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        destinations.forEach { destination ->
            TabBarItem(
                destination = destination,
                selected = destination.route == selectedRoute,
                onClick = { onSelect(destination) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun TabBarItem(
    destination: BottomDestination,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val haptics = LocalHapticFeedback.current
    val contentColor by animateColorAsState(
        targetValue = if (selected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        label = "tabItemColor",
    )
    val itemScale by animateFloatAsState(
        targetValue = if (selected) 1f else 0.94f,
        label = "tabItemScale",
    )

    Column(
        modifier = modifier
            .scale(itemScale)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) {
                haptics.performHapticFeedback(Haptics.Tap)
                onClick()
            }
            .padding(vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = if (selected) destination.selectedIcon else destination.unselectedIcon,
            contentDescription = stringResource(destination.labelRes),
            tint = contentColor,
            modifier = Modifier.size(24.dp),
        )
        Text(
            text = stringResource(destination.labelRes),
            color = contentColor,
            fontSize = 10.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            maxLines = 1,
        )
    }
}
