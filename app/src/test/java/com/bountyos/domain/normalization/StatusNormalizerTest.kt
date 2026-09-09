package com.bountyos.domain.normalization

import com.bountyos.domain.model.SubmissionStatus
import org.junit.Assert.assertEquals
import org.junit.Test

class StatusNormalizerTest {

    @Test
    fun givenHackerOneState_whenNormalizing_thenMapsToCanonicalStatus() {
        assertEquals(SubmissionStatus.OPEN, StatusNormalizer.fromHackerOne("new"))
        assertEquals(SubmissionStatus.TRIAGED, StatusNormalizer.fromHackerOne("pending-program-review"))
        assertEquals(SubmissionStatus.TRIAGED, StatusNormalizer.fromHackerOne("triaged"))
        assertEquals(SubmissionStatus.ACTION_REQUIRED, StatusNormalizer.fromHackerOne("needs-more-info"))
        assertEquals(SubmissionStatus.RETESTING, StatusNormalizer.fromHackerOne("retesting"))
        assertEquals(SubmissionStatus.RESOLVED, StatusNormalizer.fromHackerOne("resolved"))
        assertEquals(SubmissionStatus.REJECTED, StatusNormalizer.fromHackerOne("not-applicable"))
        assertEquals(SubmissionStatus.INFORMATIVE, StatusNormalizer.fromHackerOne("informative"))
        assertEquals(SubmissionStatus.DUPLICATE, StatusNormalizer.fromHackerOne("duplicate"))
        assertEquals(SubmissionStatus.REJECTED, StatusNormalizer.fromHackerOne("spam"))
    }

    @Test
    fun givenUnknownHackerOneState_whenNormalizing_thenReturnsUnknown() {
        assertEquals(SubmissionStatus.UNKNOWN, StatusNormalizer.fromHackerOne("unexpected"))
        assertEquals(SubmissionStatus.UNKNOWN, StatusNormalizer.fromHackerOne(null))
    }

    @Test
    fun givenBugcrowdState_whenNormalizing_thenMapsToCanonicalStatus() {
        assertEquals(SubmissionStatus.OPEN, StatusNormalizer.fromBugcrowd("new"))
        assertEquals(SubmissionStatus.TRIAGED, StatusNormalizer.fromBugcrowd("triaged"))
        assertEquals(SubmissionStatus.TRIAGED, StatusNormalizer.fromBugcrowd("unresolved"))
        assertEquals(SubmissionStatus.RESOLVED, StatusNormalizer.fromBugcrowd("resolved"))
        assertEquals(SubmissionStatus.INFORMATIVE, StatusNormalizer.fromBugcrowd("informational"))
        assertEquals(SubmissionStatus.REJECTED, StatusNormalizer.fromBugcrowd("out-of-scope"))
        assertEquals(SubmissionStatus.REJECTED, StatusNormalizer.fromBugcrowd("not-reproducible"))
        assertEquals(SubmissionStatus.REJECTED, StatusNormalizer.fromBugcrowd("not-applicable"))
    }

    @Test
    fun givenUnknownBugcrowdState_whenNormalizing_thenReturnsUnknown() {
        assertEquals(SubmissionStatus.UNKNOWN, StatusNormalizer.fromBugcrowd("unexpected"))
        assertEquals(SubmissionStatus.UNKNOWN, StatusNormalizer.fromBugcrowd(null))
    }
}
