package com.bountyos.domain.normalization

import com.bountyos.domain.model.SubmissionStatus
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AttentionCalculatorTest {

    @Test
    fun givenActionableStatuses_thenRequiresAttention() {
        assertTrue(AttentionCalculator.requiresAttention(SubmissionStatus.ACTION_REQUIRED))
        assertTrue(AttentionCalculator.requiresAttention(SubmissionStatus.RETESTING))
    }

    @Test
    fun givenNonActionableStatuses_thenDoesNotRequireAttention() {
        assertFalse(AttentionCalculator.requiresAttention(SubmissionStatus.OPEN))
        assertFalse(AttentionCalculator.requiresAttention(SubmissionStatus.TRIAGED))
        assertFalse(AttentionCalculator.requiresAttention(SubmissionStatus.RESOLVED))
        assertFalse(AttentionCalculator.requiresAttention(SubmissionStatus.REJECTED))
        assertFalse(AttentionCalculator.requiresAttention(SubmissionStatus.INFORMATIVE))
        assertFalse(AttentionCalculator.requiresAttention(SubmissionStatus.DUPLICATE))
        assertFalse(AttentionCalculator.requiresAttention(SubmissionStatus.UNKNOWN))
    }
}
