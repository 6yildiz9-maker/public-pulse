package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.Celebrity
import com.example.data.model.CelebrityCategory
import com.example.data.model.ObjectionNote

@Entity(tableName = "celebrities")
data class CelebrityEntity(
    @PrimaryKey val id: String,
    val name: String,
    val category: String, // OYUNCU, MUZISYEN, SPORCU, FENOMEN
    val profession: String,
    val bio: String,
    val wikiTitle: String,
    val wikiMonthlyViews: Long,
    val ratingsCompact: String, // "1:3,2:6,3:14,4:88,5:420"
    val tagsCompact: String,    // "Usta Oyuncu|Saygın|Kültür Elçisi"
    val previousPeriodLikeability: Double,
    val hasReboundBadge: Boolean = false,
    val reboundStory: String? = null,
    val forcedCircuitBreaker: Boolean = false,
    val circuitBreakerReason: String? = null,
    val objectionRepresentative: String? = null,
    val objectionSubject: String? = null,
    val objectionStatement: String? = null,
    val objectionDate: String? = null,
    val recentActivity: String = "",
    val avatarEmojiOrSymbol: String = "⭐",
    val primaryColorHex: Long = 0xFFA855F7,
    val lastScannedAt: Long = System.currentTimeMillis()
) {
    fun toDomain(): Celebrity {
        val cat = try {
            CelebrityCategory.valueOf(category)
        } catch (e: Exception) {
            CelebrityCategory.OYUNCU
        }

        val ratingsMap = mutableMapOf<Int, Int>()
        if (ratingsCompact.isNotBlank()) {
            ratingsCompact.split(",").forEach { part ->
                val tokens = part.split(":")
                if (tokens.size == 2) {
                    val star = tokens[0].toIntOrNull() ?: 1
                    val count = tokens[1].toIntOrNull() ?: 0
                    ratingsMap[star] = count
                }
            }
        }
        if (ratingsMap.isEmpty()) {
            ratingsMap[1] = 0
            ratingsMap[2] = 0
            ratingsMap[3] = 0
            ratingsMap[4] = 0
            ratingsMap[5] = 0
        }

        val tagsList = if (tagsCompact.isNotBlank()) {
            tagsCompact.split("|")
        } else emptyList()

        val objection = if (!objectionStatement.isNullOrBlank()) {
            ObjectionNote(
                submittedDate = objectionDate ?: "Kayıtlı",
                representative = objectionRepresentative ?: "Yetkili Temsilci",
                subject = objectionSubject ?: "Veri & Skor İtirazı",
                statement = objectionStatement,
                status = "İncelendi & Şeffaf Kayıt Yayında"
            )
        } else null

        return Celebrity(
            id = id,
            name = name,
            category = cat,
            profession = profession,
            bio = bio,
            wikiTitle = wikiTitle,
            wikiMonthlyViews = wikiMonthlyViews,
            ratings = ratingsMap,
            tags = tagsList,
            previousPeriodLikeability = previousPeriodLikeability,
            hasReboundBadge = hasReboundBadge,
            reboundStory = reboundStory,
            forcedCircuitBreaker = forcedCircuitBreaker,
            circuitBreakerReason = circuitBreakerReason,
            objectionNote = objection,
            recentActivity = recentActivity,
            avatarEmojiOrSymbol = avatarEmojiOrSymbol,
            primaryColorHex = primaryColorHex
        )
    }

    companion object {
        fun fromDomain(domain: Celebrity): CelebrityEntity {
            val ratingsStr = domain.ratings.entries.joinToString(",") { "${it.key}:${it.value}" }
            val tagsStr = domain.tags.joinToString("|")

            return CelebrityEntity(
                id = domain.id,
                name = domain.name,
                category = domain.category.name,
                profession = domain.profession,
                bio = domain.bio,
                wikiTitle = domain.wikiTitle,
                wikiMonthlyViews = domain.wikiMonthlyViews,
                ratingsCompact = ratingsStr,
                tagsCompact = tagsStr,
                previousPeriodLikeability = domain.previousPeriodLikeability,
                hasReboundBadge = domain.hasReboundBadge,
                reboundStory = domain.reboundStory,
                forcedCircuitBreaker = domain.forcedCircuitBreaker,
                circuitBreakerReason = domain.circuitBreakerReason,
                objectionRepresentative = domain.objectionNote?.representative,
                objectionSubject = domain.objectionNote?.subject,
                objectionStatement = domain.objectionNote?.statement,
                objectionDate = domain.objectionNote?.submittedDate,
                recentActivity = domain.recentActivity,
                avatarEmojiOrSymbol = domain.avatarEmojiOrSymbol,
                primaryColorHex = domain.primaryColorHex,
                lastScannedAt = System.currentTimeMillis()
            )
        }
    }
}
