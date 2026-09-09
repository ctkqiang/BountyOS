package com.bountyos.domain.aggregation

import com.bountyos.domain.model.Provider
import com.bountyos.domain.model.SubmissionStatus
import org.junit.Assert.assertEquals
import org.junit.Test

class DashboardStatsCalculatorTest {

    @Test
    fun givenMixedSubmissions_whenCalculating_thenCountsAreCorrect() {
        val submissions = listOf(
            submission(provider = Provider.HACKERONE, status = SubmissionStatus.OPEN),
            submission(provider = Provider.HACKERONE, status = SubmissionStatus.ACTION_REQUIRED),
            submission(provider = Provider.BUGCROWD, status = SubmissionStatus.RETESTING),
        )

        val stats = DashboardStatsCalculator.calculate(submissions)

        assertEquals(3, stats.submissionCount)
        assertEquals(2, stats.hackerOneCount)
        assertEquals(1, stats.bugcrowdCount)
        assertEquals(2, stats.attentionCount)
    }

    @Test
    fun givenNoSubmissions_whenCalculating_thenReturnsZeroStats() {
        val stats = DashboardStatsCalculator.calculate(emptyList())

        assertEquals(0, stats.submissionCount)
        assertEquals(0, stats.attentionCount)
        assertEquals(emptyList<RewardTotal>(), stats.totalRewards)
    }
}
