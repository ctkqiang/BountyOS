package com.bountyos.domain.model

/**
 * 主题模式（用户偏好）。
 *
 * 该偏好由 data 层的 [com.bountyos.data.settings.ThemePreferenceStore]
 * 持久化，由 ui 层的主题消费，因此放在 domain 层以保持依赖方向正确。
 */
enum class ThemeMode {
    /** 跟随系统深浅色设置。 */
    SYSTEM,

    /** 始终使用浅色主题。 */
    LIGHT,

    /** 始终使用深色主题。 */
    DARK,
}
