package com.example.wmfunbett2026.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.wmfunbett2026.data.local.entity.MatchEntity

@Dao
interface MatchDao {
    @Query("SELECT * FROM matches ORDER BY dayLabel, dateTimeLabel")
    fun getAll(): List<MatchEntity>

    @Query("SELECT * FROM matches WHERE id = :id LIMIT 1")
    fun getById(id: String): MatchEntity?

    @Query("SELECT * FROM matches WHERE leagueId = :leagueId")
    fun getByLeagueId(leagueId: String): List<MatchEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(match: MatchEntity)

    @Update
    fun update(match: MatchEntity)

    @Query("DELETE FROM matches WHERE id = :id")
    fun deleteById(id: String)
}
