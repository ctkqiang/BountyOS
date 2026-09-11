package com.bountyos.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Timeline
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.ui.graphics.vector.ImageVector
import com.bountyos.R

/**
 * 底部导航目的地。
 *
 * 仅保留 5 个核心入口，避免导航项过多。Settings / Programs 等
 * 次级入口从 More 进入。每个目的地提供选中（实心）与未选中（描边）
 * 两套图标。
 */
enum class BottomDestination(
    val route: String,
    @StringRes val labelRes: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
) {
    DASHBOARD(
        route = "dashboard",
        labelRes = R.string.nav_dashboard,
        selectedIcon = Icons.Filled.Dashboard,
        unselectedIcon = Icons.Outlined.Dashboard,
    ),
    REPORTS(
        route = "reports",
        labelRes = R.string.nav_reports,
        selectedIcon = Icons.Filled.Description,
        unselectedIcon = Icons.Outlined.Description,
    ),
    TRIAGE(
        route = "triage",
        labelRes = R.string.nav_triage,
        selectedIcon = Icons.Filled.Warning,
        unselectedIcon = Icons.Outlined.Warning,
    ),
    ACTIVITY(
        route = "activity",
        labelRes = R.string.nav_activity,
        selectedIcon = Icons.Filled.Timeline,
        unselectedIcon = Icons.Outlined.Timeline,
    ),
    EXPLOITDB(
        route = "exploitdb",
        labelRes = R.string.nav_exploitdb,
        selectedIcon = Icons.Filled.BugReport,
        unselectedIcon = Icons.Outlined.BugReport,
    ),
    MORE(
        route = "more",
        labelRes = R.string.settings,
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings,
    ),
}

/** 非底部导航目的地（详情、设置等）。 */
object Route {
    const val REPORT_DETAIL = "report/{submissionId}"
    const val PROGRAMS = "programs"
    const val EXPLOIT_DETAIL = "exploit/{id}"

    fun reportDetail(submissionId: String): String = "report/$submissionId"

    fun exploitDetail(id: String): String = "exploit/$id"
}
