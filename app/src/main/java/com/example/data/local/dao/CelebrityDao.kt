package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.CelebrityEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CelebrityDao {

    @Query("SELECT * FROM celebrities ORDER BY id ASC")
    fun getAllCelebrities(): Flow<List<CelebrityEntity>>

    @Query("SELECT * FROM celebrities WHERE id = :id LIMIT 1")
    suspend fun getCelebrityById(id: String): CelebrityEntity?

    @Query("SELECT COUNT(*) FROM celebrities")
    suspend fun getCelebrityCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCelebrity(celebrity: CelebrityEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCelebrities(celebrities: List<CelebrityEntity>)

    /**
     * Kişi sayısını azami 1000 kişi ile sınırlandırır.
     * 1000'i aşarsa en az oylanan ve en eski taranan profiller budanır.
     */
    @Query("""
        DELETE FROM celebrities 
        WHERE id IN (
            SELECT id FROM celebrities 
            ORDER BY lastScannedAt ASC 
            LIMIT CASE WHEN (SELECT COUNT(*) FROM celebrities) > :maxLimit 
                       THEN (SELECT COUNT(*) FROM celebrities) - :maxLimit 
                       ELSE 0 END
        )
    """)
    suspend fun trimToMaxCount(maxLimit: Int = 1000)

    @Query("DELETE FROM celebrities WHERE id = :id")
    suspend fun deleteCelebrity(id: String)
}
