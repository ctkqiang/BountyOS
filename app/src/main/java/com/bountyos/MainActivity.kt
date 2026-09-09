package com.bountyos

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bountyos.data.settings.ThemePreferenceStore
import com.bountyos.domain.model.ThemeMode
import com.bountyos.sync.SyncScheduler
import com.bountyos.ui.navigation.BountyOsApp
import com.bountyos.ui.theme.BountyOsTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * BountyOS 唯一的宿主 Activity。
 *
 * 承载 Compose 导航图与各功能屏幕。网络、数据库与业务逻辑均位于
 * ViewModel / Repository 层，Activity 负责设置 Compose 内容、订阅
 * 主题偏好，并在启动时请求通知权限与调度周期同步。
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var themePreferenceStore: ThemePreferenceStore

    @Inject
    lateinit var syncScheduler: SyncScheduler

    private val requestNotificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) {
        // 授权结果无需特殊处理，未授权时通知会被系统静默忽略。
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
        syncScheduler.schedulePeriodicSync()

        setContent {
            val themeMode by themePreferenceStore.themeMode.collectAsStateWithLifecycle(
                initialValue = ThemeMode.SYSTEM,
            )
            BountyOsTheme(themeMode = themeMode) {
                BountyOsApp()
            }
        }
    }
}
