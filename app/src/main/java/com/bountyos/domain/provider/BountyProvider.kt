package com.bountyos.domain.provider

import com.bountyos.domain.model.Activity
import com.bountyos.domain.model.Program
import com.bountyos.domain.model.Provider
import com.bountyos.domain.model.ProviderPage
import com.bountyos.domain.model.Reward
import com.bountyos.domain.model.Submission

/**
 * 只读的 bug bounty 平台适配器契约。
 *
 * BountyOS 是一个只读的查看与同步客户端。该接口**只暴露读取操作**，
 * 故意不提供任何会修改平台数据的方法（提交、编辑、评论、改状态、
 * 改严重程度、请求复测等均不存在）。
 *
 * 每个具体平台（HackerOne、Bugcrowd）各提供一个实现。返回类型均为
 * 平台中立的领域模型，远程 DTO 与映射逻辑被封装在具体实现内部，
 * 不会泄漏到上层。
 */
interface BountyProvider {

    /** 该适配器对应的平台。 */
    val provider: Provider

    /**
     * 校验当前存储的凭证是否有效。
     *
     * @return 校验通过返回 [Result.success]，否则返回携带原因的失败结果。
     */
    suspend fun validateCredentials(): Result<Unit>

    /**
     * 拉取一页提交记录。
     *
     * @param cursor 分页游标；首次调用传 null。
     */
    suspend fun fetchSubmissions(cursor: String?): ProviderPage<Submission>

    /** 拉取单个提交的完整详情。 */
    suspend fun fetchSubmission(externalId: String): Submission

    /** 拉取某个提交的活动时间线。 */
    suspend fun fetchActivities(externalId: String): List<Activity>

    /** 拉取该用户可访问的项目列表。 */
    suspend fun fetchPrograms(): List<Program>

    /** 拉取该用户的奖励记录。 */
    suspend fun fetchRewards(): List<Reward>
}
