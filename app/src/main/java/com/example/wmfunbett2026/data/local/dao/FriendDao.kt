package com.example.wmfunbett2026.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.wmfunbett2026.data.local.entity.FriendEntity

@Dao
interface FriendDao {
    @Query("SELECT * FROM friends ORDER BY name COLLATE NOCASE")
    fun getAll(): List<FriendEntity>

    @Query("SELECT COUNT(*) FROM friends")
    fun count(): Int

    @Query("SELECT * FROM friends WHERE id = :id LIMIT 1")
    fun getById(id: String): FriendEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(friend: FriendEntity)
}
