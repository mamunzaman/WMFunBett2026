package com.example.wmfunbett2026.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.wmfunbett2026.data.local.entity.EntryEntity

@Dao
interface EntryDao {
    @Query("SELECT * FROM entries")
    fun getAll(): List<EntryEntity>

    @Query("SELECT * FROM entries WHERE tippGroupId = :tippGroupId")
    fun getByTippGroupId(tippGroupId: String): List<EntryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(entry: EntryEntity)

    @Query("DELETE FROM entries WHERE id = :id")
    fun deleteById(id: String)

    @Query("DELETE FROM entries WHERE tippGroupId = :tippGroupId AND id = :id")
    fun deleteById(tippGroupId: String, id: String)
}
