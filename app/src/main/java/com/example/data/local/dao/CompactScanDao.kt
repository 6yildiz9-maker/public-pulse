package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.CompactScanEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CompactScanDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScan(scan: CompactScanEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScans(scans: List<CompactScanEntity>)

    @Query("SELECT * FROM compact_scan_history WHERE celebrityId = :celebrityId ORDER BY epochWeek DESC LIMIT 12")
    fun getScansForCelebrity(celebrityId: String): Flow<List<CompactScanEntity>>

    @Query("SELECT * FROM compact_scan_history WHERE celebrityId = :celebrityId ORDER BY epochWeek DESC LIMIT 1")
    suspend fun getLatestScan(celebrityId: String): CompactScanEntity?

    @Query("SELECT COUNT(*) FROM compact_scan_history")
    suspend fun getTotalRecordCount(): Int

    @Query("SELECT COUNT(DISTINCT celebrityId) FROM compact_scan_history")
    suspend fun getUniqueCelebrityCount(): Int
}
