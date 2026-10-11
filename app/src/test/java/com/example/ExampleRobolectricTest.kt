package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.CelebrityCategory
import com.example.data.model.PublicMetricsCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Public Pulse", appName)
    }

    @Test
    fun `verify Bayesian likeability and polarization calculations`() {
        // High consensus case: mostly 5-stars
        val highConsensusRatings = mapOf(1 to 2, 2 to 3, 3 to 10, 4 to 85, 5 to 400)
        val bayesMean = PublicMetricsCalculator.calculateBayesianMean(
            ratings = highConsensusRatings,
            priorC = CelebrityCategory.OYUNCU.priorMean,
            smoothingM = CelebrityCategory.OYUNCU.smoothingM
        )
        val likeability = PublicMetricsCalculator.calculateLikeability(bayesMean)
        val polarization = PublicMetricsCalculator.calculatePolarization(highConsensusRatings)

        assertTrue("Likeability should be high", likeability > 80.0)
        assertTrue("Polarization should be low for consensus", polarization < 10.0)

        // Polarized case: 1-star vs 5-stars split
        val splitRatings = mapOf(1 to 150, 2 to 50, 3 to 20, 4 to 80, 5 to 200)
        val splitPolarization = PublicMetricsCalculator.calculatePolarization(splitRatings)
        // p+ = 280/500 = 0.56, p- = 200/500 = 0.40. D = 200 * 0.40 = 80.0
        assertEquals(80.0, splitPolarization, 1.0)
    }

    @Test
    fun `verify circuit breaker caps surge at 8 points`() {
        val (cappedPositive, triggeredPos) = PublicMetricsCalculator.applyCircuitBreaker(12.5)
        assertEquals(8.0, cappedPositive, 0.01)
        assertTrue(triggeredPos)

        val (cappedNegative, triggeredNeg) = PublicMetricsCalculator.applyCircuitBreaker(-15.0)
        assertEquals(-8.0, cappedNegative, 0.01)
        assertTrue(triggeredNeg)

        val (normalDelta, triggeredNormal) = PublicMetricsCalculator.applyCircuitBreaker(4.2)
        assertEquals(4.2, normalDelta, 0.01)
        org.junit.Assert.assertFalse(triggeredNormal)
    }
}
