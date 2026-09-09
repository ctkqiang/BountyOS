package com.bountyos.sync

import com.bountyos.domain.model.Submission

/**
 * 提交数据在一次同步中发生的变化。
 */
data class SubmissionChange(
    val submission: Submission,
    val kind: ChangeKind,
)

/** 变化类型。 */
enum class ChangeKind {
    /** 状态发生变化。 */
    STATUS_CHANGED,

    /** 收到新奖励。 */
    REWARD_RECEIVED,

    /** 新增提交。 */
    NEW_SUBMISSION,
}
