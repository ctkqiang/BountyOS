package com.bountyos.sync

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.bountyos.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 本地通知的发送者。
 *
 * BountyOS 无后端，无法提供真正的实时推送。通知由后台同步检测到
 * 数据变化后生成。通知文案本地化，且不包含任何凭证信息。
 */
@Singleton
class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    /*
     * 权限已在 canPostNotifications() 中显式检查，此处 SuppressLint
     * 仅为消除 lint 的跨方法追踪盲区；未授权时不会执行到 notify。
     */
    @SuppressLint("MissingPermission")
    fun notifyChanges(changes: List<SubmissionChange>) {
        if (changes.isEmpty()) return
        if (!canPostNotifications()) return

        createChannelIfNeeded()
        val manager = NotificationManagerCompat.from(context)
        changes.forEachIndexed { index, change ->
            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(changeTitle(change))
                .setContentText(changeText(change))
                .setStyle(NotificationCompat.BigTextStyle().bigText(changeText(change)))
                .setAutoCancel(true)
                .build()
            manager.notify(index, notification)
        }
    }

    /** 通知 ExploitDB 索引有新条目。 */
    @SuppressLint("MissingPermission")
    fun notifyExploitDbUpdate(newCount: Int) {
        if (newCount <= 0) return
        if (!canPostNotifications()) return

        createChannelIfNeeded()
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(context.getString(R.string.notification_exploitdb_title))
            .setContentText(context.getString(R.string.notification_exploitdb_text, newCount))
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(context).notify(EXPLOITDB_NOTIFICATION_ID, notification)
    }

    private fun changeTitle(change: SubmissionChange): String = when (change.kind) {
        ChangeKind.STATUS_CHANGED -> context.getString(R.string.notification_status_changed_title)
        ChangeKind.REWARD_RECEIVED -> context.getString(R.string.notification_reward_title)
        ChangeKind.NEW_SUBMISSION -> context.getString(R.string.notification_new_submission_title)
    }

    private fun changeText(change: SubmissionChange): String {
        val submission = change.submission
        return when (change.kind) {
            ChangeKind.STATUS_CHANGED -> context.getString(
                R.string.notification_status_changed_text,
                submission.provider.name,
                submission.externalId,
                submission.providerStatus,
            )
            ChangeKind.REWARD_RECEIVED -> context.getString(
                R.string.notification_reward_text,
                submission.externalId,
            )
            ChangeKind.NEW_SUBMISSION -> context.getString(
                R.string.notification_new_submission_text,
                submission.title,
            )
        }
    }

    private fun canPostNotifications(): Boolean =
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS,
        ) == PackageManager.PERMISSION_GRANTED

    private fun createChannelIfNeeded() {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            CHANNEL_ID,
            context.getString(R.string.notification_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT,
        )
        manager.createNotificationChannel(channel)
    }

    private companion object {
        const val CHANNEL_ID = "bountyos_sync"
        const val EXPLOITDB_NOTIFICATION_ID = 10_000
    }
}
