package com.example.wmfunbett2026.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.wmfunbett2026.data.local.entity.TippGroupEntity

@Dao
interface TippGroupDao {
    @Query("SELECT * FROM tipp_groups")
    fun getAll(): List<TippGroupEntity>

    @Query("SELECT * FROM tipp_groups WHERE id = :id LIMIT 1")
    fun getById(id: String): TippGroupEntity?

    @Query("SELECT * FROM tipp_groups WHERE matchId = :matchId")
    fun getByMatchId(matchId: String): List<TippGroupEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(tippGroup: TippGroupEntity)

    @Query("DELETE FROM tipp_groups WHERE id = :id")
    fun deleteById(id: String)
}
