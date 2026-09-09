package com.bountyos.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.Timeline
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.ui.graphics.vector.ImageVector
import com.bountyos.R

/**
 * 底部导航目的地。
 *
 * 仅保留 5 个核心入口，避免导航项过多。Settings / Programs 等
 * 次级入口从 More 进入。
 */
enum class BottomDestination(
    val route: String,
    @StringRes val labelRes: Int,
    val icon: ImageVector,
) {
    DASHBOARD("dashboard", R.string.nav_dashboard, Icons.Outlined.Dashboard),
    REPORTS("reports", R.string.nav_reports, Icons.Outlined.Description),
    TRIAGE("triage", R.string.nav_triage, Icons.Outlined.Warning),
    ACTIVITY("activity", R.string.nav_activity, Icons.Outlined.Timeline),
    MORE("more", R.string.nav_more, Icons.Outlined.MoreHoriz),
}

/** 非底部导航目的地（详情、设置等）。 */
object Route {
    const val REPORT_DETAIL = "report/{submissionId}"
    const val SETTINGS = "settings"
    const val PROGRAMS = "programs"

    fun reportDetail(submissionId: String): String = "report/$submissionId"
}
