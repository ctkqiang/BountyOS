package com.bountyos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.bountyos.ui.navigation.BountyOsApp
import com.bountyos.ui.theme.BountyOsTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * BountyOS 唯一的宿主 Activity。
 *
 * 承载 Compose 导航图与各功能屏幕。网络、数据库与业务逻辑均位于
 * ViewModel / Repository 层，Activity 只负责设置 Compose 内容。
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BountyOsTheme {
                BountyOsApp()
            }
        }
    }
}
