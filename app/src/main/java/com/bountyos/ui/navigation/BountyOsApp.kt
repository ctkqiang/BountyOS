package com.bountyos.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.bountyos.ui.activity.ActivityScreen
import com.bountyos.ui.dashboard.DashboardScreen
import com.bountyos.ui.detail.ReportDetailScreen
import com.bountyos.ui.reports.ReportsScreen
import com.bountyos.ui.settings.SettingsScreen
import com.bountyos.ui.triage.TriageScreen

/**
 * BountyOS 根 Composable。
 *
 * 承载底部导航与导航图。底部导航仅在顶层目的地显示，详情与设置
 * 等次级页面不显示底部栏。
 */
@Composable
fun BountyOsApp() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val haptics = LocalHapticFeedback.current

    val isTopLevel = currentDestination?.route in BottomDestination.entries.map { it.route }

    Scaffold(
        bottomBar = {
            if (isTopLevel) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.background,
                    tonalElevation = 0.dp,
                ) {
                    BottomDestination.entries.forEach { destination ->
                        val selected = currentDestination?.hierarchy
                            ?.any { it.route == destination.route } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                navController.navigate(destination.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (selected) destination.selectedIcon else destination.unselectedIcon,
                                    contentDescription = stringResource(destination.labelRes),
                                )
                            },
                            label = { Text(stringResource(destination.labelRes)) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            ),
                        )
                    }
                }
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomDestination.DASHBOARD.route,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(BottomDestination.DASHBOARD.route) { DashboardScreen(navController) }
            composable(BottomDestination.REPORTS.route) { ReportsScreen(navController) }
            composable(BottomDestination.TRIAGE.route) { TriageScreen(navController) }
            composable(BottomDestination.ACTIVITY.route) { ActivityScreen() }
            composable(BottomDestination.MORE.route) { SettingsScreen() }
            composable(Route.REPORT_DETAIL) { ReportDetailScreen(navController) }
        }
    }
}
