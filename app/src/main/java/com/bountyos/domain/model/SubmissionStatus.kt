package com.bountyos.domain.model

/**
 * BountyOS 内部的规范化提交状态。
 *
 * 各平台的状态命名与语义并不一致（例如 HackerOne 的 `needs-more-info`
 * 对应 Bugcrowd 语义上的等待补充信息）。因此 BountyOS 维护一套
 * 与平台无关的规范状态，用于统一的筛选、注意力计算与展示。
 *
 * 原始的平台状态值始终保留在 [Submission.providerStatus] 中，
 * 规范化过程不丢失任何原始信息。
 */
enum class SubmissionStatus {
    /** 已提交、尚未被平台处理的开放状态。 */
    OPEN,

    /** 已被平台确认有效（或进入待客户审查阶段）。 */
    TRIAGED,

    /** 需要研究者补充信息或采取行动。 */
    ACTION_REQUIRED,

    /** 正在复测中。 */
    RETESTING,

    /** 已解决并关闭。 */
    RESOLVED,

    /** 被平台驳回（不适用 / 超出范围 / 无法复现 / 垃圾）。 */
    REJECTED,

    /** 与已有报告重复。 */
    DUPLICATE,

    /** 提供了有效信息但无需进一步处理。 */
    INFORMATIVE,

    /** 无法映射到已知状态，需保留原始值进行展示。 */
    UNKNOWN,
}
