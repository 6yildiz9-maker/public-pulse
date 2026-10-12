package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.UserVoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserVoteDao {

    @Query("SELECT * FROM user_votes")
    fun getAllVotes(): Flow<List<UserVoteEntity>>

    @Query("SELECT * FROM user_votes WHERE celebrityId = :celebrityId LIMIT 1")
    suspend fun getVoteForCelebrity(celebrityId: String): UserVoteEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVote(vote: UserVoteEntity)
}
