package com.example.wmfunbett2026.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.wmfunbett2026.data.local.dao.EntryDao
import com.example.wmfunbett2026.data.local.dao.FriendDao
import com.example.wmfunbett2026.data.local.dao.LeagueDao
import com.example.wmfunbett2026.data.local.dao.MatchDao
import com.example.wmfunbett2026.data.local.dao.TippGroupDao
import com.example.wmfunbett2026.data.local.entity.EntryEntity
import com.example.wmfunbett2026.data.local.entity.FriendEntity
import com.example.wmfunbett2026.data.local.entity.LeagueEntity
import com.example.wmfunbett2026.data.local.entity.MatchEntity
import com.example.wmfunbett2026.data.local.entity.TippGroupEntity

@Database(
    entities = [
        LeagueEntity::class,
        MatchEntity::class,
        TippGroupEntity::class,
        EntryEntity::class,
        FriendEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class FunBettDatabase : RoomDatabase() {
    abstract fun leagueDao(): LeagueDao
    abstract fun matchDao(): MatchDao
    abstract fun tippGroupDao(): TippGroupDao
    abstract fun entryDao(): EntryDao
    abstract fun friendDao(): FriendDao

    companion object {
        private const val DATABASE_NAME = "funbett.db"

        @Volatile
        private var instance: FunBettDatabase? = null

        fun getInstance(context: Context): FunBettDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    FunBettDatabase::class.java,
                    DATABASE_NAME
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .allowMainThreadQueries()
                    .build()
                    .also { instance = it }
            }
    }
}
