package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_votes")
data class UserVoteEntity(
    @PrimaryKey val celebrityId: String,
    val knows: String,
    val rating: Int,
    val tagsCompact: String,
    val votedAt: Long = System.currentTimeMillis()
)
