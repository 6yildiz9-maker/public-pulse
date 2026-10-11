package com.example.data.model

data class ObjectionNote(
    val submittedDate: String,
    val representative: String,
    val subject: String,
    val statement: String,
    val status: String = "Yayında (Şeffaf Kayıt)"
)

data class Celebrity(
    val id: String,
    val name: String,
    val category: CelebrityCategory,
    val profession: String,
    val bio: String,
    val wikiTitle: String,
    val wikiMonthlyViews: Long,
    val ratings: Map<Int, Int>, // 1..5 stars -> vote count
    val tags: List<String>,
    val previousPeriodLikeability: Double,
    val hasReboundBadge: Boolean = false,
    val reboundStory: String? = null,
    val forcedCircuitBreaker: Boolean = false,
    val circuitBreakerReason: String? = null,
    val objectionNote: ObjectionNote? = null,
    val recentActivity: String = "",
    val avatarEmojiOrSymbol: String = "⭐",
    val primaryColorHex: Long = 0xFFA855F7
) {
    val calculationDetails: MetricCalculationDetails
        get() = PublicMetricsCalculator.getFullCalculationDetails(
            ratings = ratings,
            category = category,
            wikiViews = wikiMonthlyViews,
            previousLikeability = previousPeriodLikeability
        )

    val voteCount: Int
        get() = calculationDetails.n

    val likeability: Double
        get() = calculationDetails.likeabilityL

    val popularity: Double
        get() = calculationDetails.popularityP

    val polarization: Double
        get() = calculationDetails.polarizationD

    val momentum: Double
        get() = calculationDetails.finalMomentum

    val isCircuitBreakerActive: Boolean
        get() = forcedCircuitBreaker || calculationDetails.circuitBreakerTriggered

    val confidenceLevel: ConfidenceLevel
        get() = calculationDetails.confidence
}
