package com.bountyos.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.bountyos.ui.activity.ActivityScreen
import com.bountyos.ui.dashboard.DashboardScreen
import com.bountyos.ui.detail.ReportDetailScreen
import com.bountyos.ui.exploitdb.ExploitDbScreen
import com.bountyos.ui.exploitdb.ExploitDetailScreen
import com.bountyos.ui.mitre.MitreScreen
import com.bountyos.ui.reports.ReportsScreen
import com.bountyos.ui.settings.SettingsScreen
import com.bountyos.ui.theme.GlassBackground
import com.bountyos.ui.theme.glassEffect
import com.bountyos.ui.triage.TriageScreen
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze

/**
 * BountyOS 根 Composable。
 *
 * 承载底部导航与导航图。底部导航仅在顶层目的地显示，详情等次级
 * 页面不显示底部栏。底栏为自绘的 iOS 风格 tab bar（非 Material3）。
 *
 * 内容层用 [haze] 标记为模糊来源并延伸到屏幕底部，底栏用 [glassEffect]
 * 作为玻璃层，模糊其后滚动的内容。因此 Scaffold 的窗口 inset 交由
 * 内容自行处理。
 */
@Composable
fun BountyOsApp() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val hazeState = remember { HazeState() }

    val isTopLevel = currentDestination?.route in BottomDestination.entries.map { it.route }

    Box(modifier = Modifier.fillMaxSize()) {
        GlassBackground()
        Scaffold(
            containerColor = Color.Transparent,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            bottomBar = {
                if (isTopLevel) {
                    BountyTabBar(
                        destinations = BottomDestination.entries,
                        selectedRoute = currentDestination?.route,
                        onSelect = { destination ->
                            navController.navigate(destination.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        modifier = Modifier.glassEffect(
                            state = hazeState,
                            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                        ),
                    )
                }
            },
        ) { _ ->
            NavHost(
                navController = navController,
                startDestination = BottomDestination.DASHBOARD.route,
                modifier = Modifier
                    .haze(hazeState)
                    .statusBarsPadding()
                    .navigationBarsPadding(),
            ) {
                composable(BottomDestination.DASHBOARD.route) { DashboardScreen(navController) }
                composable(BottomDestination.REPORTS.route) { ReportsScreen(navController) }
                composable(BottomDestination.TRIAGE.route) { TriageScreen(navController) }
                composable(BottomDestination.ACTIVITY.route) { ActivityScreen() }
                composable(BottomDestination.EXPLOITDB.route) { ExploitDbScreen(navController) }
                composable(BottomDestination.MORE.route) { SettingsScreen() }
                composable(Route.REPORT_DETAIL) { ReportDetailScreen(navController) }
                composable(Route.EXPLOIT_DETAIL) { ExploitDetailScreen(navController) }
                composable(Route.MITRE) { MitreScreen(navController) }
            }
        }
    }
}
