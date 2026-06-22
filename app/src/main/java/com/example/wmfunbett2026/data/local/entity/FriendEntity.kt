package com.example.wmfunbett2026.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "friends",
    indices = [Index(value = ["name"], unique = true)]
)
data class FriendEntity(
    @PrimaryKey val id: String,
    val name: String,
    val note: String?,
    val createdAt: Long
)
