package com.example.data.model

import kotlin.math.ln
import kotlin.math.max
import kotlin.math.min

enum class ConfidenceLevel(val label: String, val description: String) {
    YUKSEK("Yüksek Güven", "Geniş örneklem (150+ oy), doğrulanmış veri kapsamı ve istikrarlı dağılım."),
    ORTA("Orta Güven", "Orta ölçekli örneklem (40-150 oy), güvenilir trend sinyali."),
    DUSUK("Düşük Güven", "Sınırlı örneklem (10-39 oy), Bayesçi yumuşatma ağırlıkta."),
    YETERSIZ("Yetersiz Veri", "Yetersiz örneklem (<10 oy). Henüz istatistiksel geçerlilik oluşmadı.")
}

data class MetricCalculationDetails(
    val n: Int,
    val rawAverage: Double,
    val categoryPriorC: Double,
    val smoothingM: Double,
    val bayesianMean: Double,
    val likeabilityL: Double,
    val pPositive: Double,
    val pNegative: Double,
    val polarizationD: Double,
    val wikiViews: Long,
    val popularityP: Double,
    val rawMomentum: Double,
    val finalMomentum: Double,
    val circuitBreakerTriggered: Boolean,
    val circuitBreakerCap: Double = 8.0,
    val confidence: ConfidenceLevel
)

object PublicMetricsCalculator {

    /**
     * Bayesçi Düzeltilmiş Ortalama:
     * r̄_düzeltilmiş = (n * r̄ + m * C) / (n + m)
     */
    fun calculateBayesianMean(
        ratings: Map<Int, Int>,
        priorC: Double,
        smoothingM: Double
    ): Double {
        val n = ratings.values.sum()
        if (n == 0) return priorC

        var totalStars = 0.0
        for ((star, count) in ratings) {
            totalStars += star * count
        }
        val rawAverage = totalStars / n.toDouble()
        return (n * rawAverage + smoothingM * priorC) / (n + smoothingM)
    }

    /**
     * Sevilirlik Skoru (0-100):
     * L = 25 * (r̄_düzeltilmiş - 1)
     */
    fun calculateLikeability(bayesianMean: Double): Double {
        val score = 25.0 * (bayesianMean - 1.0)
        return score.coerceIn(0.0, 100.0)
    }

    /**
     * Kutuplaşma Ölçümü (0-100) — Yargı değil, karşıt görüşlerin dağılımı:
     * D = 200 * min(p₊, p₋)
     * p₊: 4-5 yıldız verenlerin oranı
     * p₋: 1-2 yıldız verenlerin oranı
     * Nötr oylar (3 yıldız) kutuplaşmanın tarafı sayılmaz.
     */
    fun calculatePolarization(ratings: Map<Int, Int>): Double {
        val n = ratings.values.sum()
        if (n == 0) return 0.0

        val posCount = (ratings[4] ?: 0) + (ratings[5] ?: 0)
        val negCount = (ratings[1] ?: 0) + (ratings[2] ?: 0)

        val pPos = posCount.toDouble() / n.toDouble()
        val pNeg = negCount.toDouble() / n.toDouble()

        val pol = 200.0 * min(pPos, pNeg)
        return pol.coerceIn(0.0, 100.0)
    }

    /**
     * Popülerlik Skoru (0-100):
     * Wikipedia sayfa görüntülenmeleri + kategori içi logaritmik ölçeklendirme
     */
    fun calculatePopularity(wikiViews: Long, maxExpectedViews: Long): Double {
        if (wikiViews <= 0) return 5.0
        // Logarithmic scale so large traffic differences stay bounded and intuitive
        val minLog = ln(1000.0)
        val maxLog = ln(max(maxExpectedViews.toDouble(), 10000.0))
        val currentLog = ln(max(wikiViews.toDouble(), 1000.0))

        val normalized = (currentLog - minLog) / (maxLog - minLog)
        val score = (normalized * 90.0) + 10.0 // 10 to 100 range
        return score.coerceIn(5.0, 99.0)
    }

    fun calculateConfidence(n: Int): ConfidenceLevel {
        return when {
            n >= 150 -> ConfidenceLevel.YUKSEK
            n >= 40 -> ConfidenceLevel.ORTA
            n >= 10 -> ConfidenceLevel.DUSUK
            else -> ConfidenceLevel.YETERSIZ
        }
    }

    /**
     * Devre kesici kuralı: 24 saatlik değişim ±8 puanı aşamaz.
     */
    fun applyCircuitBreaker(rawMomentum: Double): Pair<Double, Boolean> {
        val maxDelta = 8.0
        return if (rawMomentum > maxDelta) {
            maxDelta to true
        } else if (rawMomentum < -maxDelta) {
            -maxDelta to true
        } else {
            rawMomentum to false
        }
    }

    fun getFullCalculationDetails(
        ratings: Map<Int, Int>,
        category: CelebrityCategory,
        wikiViews: Long,
        previousLikeability: Double
    ): MetricCalculationDetails {
        val n = ratings.values.sum()
        var totalStars = 0.0
        for ((star, count) in ratings) {
            totalStars += star * count
        }
        val rawAverage = if (n > 0) totalStars / n.toDouble() else category.priorMean
        val bayesianMean = calculateBayesianMean(ratings, category.priorMean, category.smoothingM)
        val likeability = calculateLikeability(bayesianMean)

        val posCount = (ratings[4] ?: 0) + (ratings[5] ?: 0)
        val negCount = (ratings[1] ?: 0) + (ratings[2] ?: 0)
        val pPos = if (n > 0) posCount.toDouble() / n.toDouble() else 0.0
        val pNeg = if (n > 0) negCount.toDouble() / n.toDouble() else 0.0
        val polarization = if (n > 0) (200.0 * min(pPos, pNeg)).coerceIn(0.0, 100.0) else 0.0

        val popularity = calculatePopularity(wikiViews, category.maxExpectedViews)
        val rawMomentum = likeability - previousLikeability
        val (finalMomentum, circuitBreakerActive) = applyCircuitBreaker(rawMomentum)
        val confidence = calculateConfidence(n)

        return MetricCalculationDetails(
            n = n,
            rawAverage = rawAverage,
            categoryPriorC = category.priorMean,
            smoothingM = category.smoothingM,
            bayesianMean = bayesianMean,
            likeabilityL = likeability,
            pPositive = pPos,
            pNegative = pNeg,
            polarizationD = polarization,
            wikiViews = wikiViews,
            popularityP = popularity,
            rawMomentum = rawMomentum,
            finalMomentum = finalMomentum,
            circuitBreakerTriggered = circuitBreakerActive,
            confidence = confidence
        )
    }
}
