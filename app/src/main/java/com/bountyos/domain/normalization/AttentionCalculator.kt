package com.bountyos.domain.normalization

import com.bountyos.domain.model.SubmissionStatus

/**
 * 计算某个规范化状态是否需要研究者关注。
 *
 * 「关注」指该报告需要研究者的后续行动，用于填充 Triage / Attention
 * Inbox。BountyOS 是只读客户端，因此这里的「关注」仅用于提示，
 * 用户仍需跳转到原平台进行操作。
 */
object AttentionCalculator {

    /**
     * 判断状态是否属于需要关注的状态。
     *
     * 需要关注的状态：
     *  - [SubmissionStatus.ACTION_REQUIRED] 平台要求补充信息
     *  - [SubmissionStatus.RETESTING] 等待研究者复测
     */
    fun requiresAttention(status: SubmissionStatus): Boolean = when (status) {
        SubmissionStatus.ACTION_REQUIRED,
        SubmissionStatus.RETESTING -> true

        else -> false
    }
}
