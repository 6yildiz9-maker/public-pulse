package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.CelebrityDao
import com.example.data.local.dao.CompactScanDao
import com.example.data.local.dao.UserVoteDao
import com.example.data.local.entity.CelebrityEntity
import com.example.data.local.entity.CompactScanEntity
import com.example.data.local.entity.UserVoteEntity

@Database(
    entities = [
        CelebrityEntity::class,
        CompactScanEntity::class,
        UserVoteEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun celebrityDao(): CelebrityDao
    abstract fun compactScanDao(): CompactScanDao
    abstract fun userVoteDao(): UserVoteDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "public_pulse_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
