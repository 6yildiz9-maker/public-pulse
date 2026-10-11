package com.example.data.repository

import com.example.data.model.Celebrity
import com.example.data.model.CelebrityCatalog
import com.example.data.model.ObjectionNote
import com.example.data.model.PublicMetricsCalculator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class UserVoteRecord(
    val celebrityId: String,
    val knows: String,
    val rating: Int,
    val tags: List<String>,
    val timestamp: Long = System.currentTimeMillis()
)

class PulseRepository {

    private val _celebrities = MutableStateFlow<List<Celebrity>>(CelebrityCatalog.sampleCelebrities)
    val celebrities: StateFlow<List<Celebrity>> = _celebrities.asStateFlow()

    private val _userVotes = MutableStateFlow<Map<String, UserVoteRecord>>(emptyMap())
    val userVotes: StateFlow<Map<String, UserVoteRecord>> = _userVotes.asStateFlow()

    fun getCelebrity(id: String): Celebrity? {
        return _celebrities.value.find { it.id == id }
    }

    fun hasUserVoted(celebrityId: String): Boolean {
        return _userVotes.value.containsKey(celebrityId)
    }

    fun submitVote(
        celebrityId: String,
        knows: String,
        rating: Int,
        selectedTags: List<String>
    ): Boolean {
        val currentCeleb = getCelebrity(celebrityId) ?: return false

        // Record user vote
        _userVotes.update { current ->
            current + (celebrityId to UserVoteRecord(
                celebrityId = celebrityId,
                knows = knows,
                rating = rating,
                tags = selectedTags
            ))
        }

        // Update celebrity ratings count
        val currentRatings = currentCeleb.ratings.toMutableMap()
        val existingCount = currentRatings[rating] ?: 0
        currentRatings[rating] = existingCount + 1

        val updatedTags = (currentCeleb.tags + selectedTags).distinct()

        // Check if delta triggers circuit breaker
        val newBayesianMean = PublicMetricsCalculator.calculateBayesianMean(
            ratings = currentRatings,
            priorC = currentCeleb.category.priorMean,
            smoothingM = currentCeleb.category.smoothingM
        )
        val newLikeability = PublicMetricsCalculator.calculateLikeability(newBayesianMean)
        val rawDelta = newLikeability - currentCeleb.previousPeriodLikeability
        val circuitBreakerTriggered = rawDelta > 8.0 || rawDelta < -8.0

        val updatedCeleb = currentCeleb.copy(
            ratings = currentRatings,
            tags = updatedTags,
            forcedCircuitBreaker = currentCeleb.forcedCircuitBreaker || circuitBreakerTriggered,
            circuitBreakerReason = if (circuitBreakerTriggered) {
                "Hızlı oy hareketi nedeniyle devre kesici devrede (±8.0 puan sınırı korundu)."
            } else currentCeleb.circuitBreakerReason
        )

        _celebrities.update { list ->
            list.map { if (it.id == celebrityId) updatedCeleb else it }
        }

        return true
    }

    fun submitObjection(
        celebrityId: String,
        representative: String,
        subject: String,
        statement: String
    ): Boolean {
        val currentCeleb = getCelebrity(celebrityId) ?: return false
        val objection = ObjectionNote(
            submittedDate = "Bugün (11.10.2026)",
            representative = representative.ifBlank { "Temsilci / Doğrulama Talebi" },
            subject = subject.ifBlank { "Veri ve Skorlama İtirazı" },
            statement = statement,
            status = "İncelendi & Şeffaf Kayıt Yayında"
        )

        val updatedCeleb = currentCeleb.copy(objectionNote = objection)
        _celebrities.update { list ->
            list.map { if (it.id == celebrityId) updatedCeleb else it }
        }
        return true
    }
}
