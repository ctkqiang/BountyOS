package com.bountyos.ui.navigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.bountyos.R

/**
 * More 屏幕。
 *
 * 承载次级入口（Settings 等），避免底部导航项过多。
 */
@Composable
fun MoreScreen(navController: NavController) {
    Column(modifier = Modifier.fillMaxSize()) {
        ListItem(
            modifier = Modifier.clickable { navController.navigate(Route.SETTINGS) },
            headlineContent = { Text(stringResource(R.string.settings)) },
            leadingContent = {
                Icon(Icons.Outlined.Settings, contentDescription = null)
            },
        )
        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
    }
}
