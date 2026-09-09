package com.bountyos.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bountyos.domain.model.Submission
import com.bountyos.domain.model.SubmissionStatus
import com.bountyos.domain.repository.SubmissionRepository
import com.bountyos.domain.repository.SyncCoordinator
import com.bountyos.domain.repository.SyncResult
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

/**
 * 后台同步 Worker。
 *
 * 流程：同步前快照 -> 执行同步 -> 同步后快照 -> 检测变化 -> 发出本地
 * 通知。同步失败时返回重试，交由 WorkManager 退避调度。
 */
@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val syncCoordinator: SyncCoordinator,
    private val submissionRepository: SubmissionRepository,
    private val notificationHelper: NotificationHelper,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val before = snapshot()
        val syncResult = syncCoordinator.synchronize()
        val after = submissionRepository.observeSubmissions().first()

        val changes = detectChanges(before, after)

        return when (syncResult) {
            is SyncResult.Success -> {
                notificationHelper.notifyChanges(changes)
                Result.success()
            }
            is SyncResult.Failed -> Result.retry()
        }
    }

    private suspend fun snapshot(): Map<String, SubmissionSnapshot> =
        submissionRepository.observeSubmissions().first().associate { submission ->
            submission.id to SubmissionSnapshot(
                status = submission.status,
                providerStatus = submission.providerStatus,
                rewardAmount = submission.reward?.amount,
            )
        }

    private fun detectChanges(
        before: Map<String, SubmissionSnapshot>,
        after: List<Submission>,
    ): List<SubmissionChange> = after.mapNotNull { submission ->
        val previous = before[submission.id]
        when {
            previous == null -> SubmissionChange(submission, ChangeKind.NEW_SUBMISSION)
            previous.status != submission.status ->
                SubmissionChange(submission, ChangeKind.STATUS_CHANGED)
            previous.rewardAmount == null && submission.reward?.amount != null ->
                SubmissionChange(submission, ChangeKind.REWARD_RECEIVED)
            else -> null
        }
    }

    private data class SubmissionSnapshot(
        val status: SubmissionStatus,
        val providerStatus: String,
        val rewardAmount: String?,
    )
}
