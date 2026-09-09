package com.bountyos.domain.aggregation

import com.bountyos.domain.model.Provider
import com.bountyos.domain.model.Reward
import com.bountyos.domain.model.Submission
import com.bountyos.domain.model.SubmissionStatus
import org.junit.Assert.assertEquals
import org.junit.Test

class RewardAggregatorTest {

    @Test
    fun givenRewardsAcrossCurrencies_whenAggregating_thenGroupsAndSums() {
        val submissions = listOf(
            submission(reward = Reward("100.00", "USD")),
            submission(reward = Reward("50.50", "USD")),
            submission(reward = Reward("20.00", "EUR")),
        )

        val totals = RewardAggregator.aggregate(submissions)

        assertEquals(2, totals.size)
        assertEquals("150.50", totals.first { it.currency == "USD" }.amount)
        assertEquals("20.00", totals.first { it.currency == "EUR" }.amount)
    }

    @Test
    fun givenMissingCurrency_whenAggregating_thenUsesUnknownMarker() {
        val submissions = listOf(
            submission(reward = Reward("10.00", null)),
        )

        val totals = RewardAggregator.aggregate(submissions)

        assertEquals(1, totals.size)
        assertEquals(RewardAggregator.UNKNOWN_CURRENCY, totals.first().currency)
    }

    @Test
    fun givenNoRewards_whenAggregating_thenReturnsEmpty() {
        assertEquals(emptyList<RewardTotal>(), RewardAggregator.aggregate(emptyList()))
    }
}

internal fun submission(
    reward: Reward? = null,
    status: SubmissionStatus = SubmissionStatus.OPEN,
    provider: Provider = Provider.HACKERONE,
): Submission = Submission(
    id = "${provider.name}:1",
    provider = provider,
    externalId = "1",
    programName = null,
    title = "Test submission",
    status = status,
    providerStatus = "new",
    severity = null,
    providerSeverity = null,
    weakness = null,
    submittedAt = null,
    updatedAt = null,
    resolvedAt = null,
    reward = reward,
    canonicalUrl = null,
    vulnerabilityInformation = null,
)
