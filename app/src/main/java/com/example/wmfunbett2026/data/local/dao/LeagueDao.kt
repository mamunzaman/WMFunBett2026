package com.example.wmfunbett2026.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.wmfunbett2026.data.local.entity.LeagueEntity

@Dao
interface LeagueDao {
    @Query("SELECT * FROM leagues ORDER BY name")
    fun getAll(): List<LeagueEntity>

    @Query("SELECT * FROM leagues WHERE id = :id LIMIT 1")
    fun getById(id: String): LeagueEntity?

    @Query("SELECT COUNT(*) FROM leagues")
    fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(league: LeagueEntity)

    @Query("DELETE FROM leagues WHERE id = :id")
    fun deleteById(id: String)
}
