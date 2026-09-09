package com.bountyos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bountyos.data.settings.ThemePreferenceStore
import com.bountyos.domain.model.ThemeMode
import com.bountyos.ui.navigation.BountyOsApp
import com.bountyos.ui.theme.BountyOsTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * BountyOS 唯一的宿主 Activity。
 *
 * 承载 Compose 导航图与各功能屏幕。网络、数据库与业务逻辑均位于
 * ViewModel / Repository 层，Activity 只负责设置 Compose 内容并
 * 订阅主题偏好。
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var themePreferenceStore: ThemePreferenceStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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
