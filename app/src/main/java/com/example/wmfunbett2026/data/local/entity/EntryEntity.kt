package com.example.wmfunbett2026.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "entries",
    foreignKeys = [
        ForeignKey(
            entity = TippGroupEntity::class,
            parentColumns = ["id"],
            childColumns = ["tippGroupId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("tippGroupId")]
)
data class EntryEntity(
    @PrimaryKey val id: String,
    val tippGroupId: String,
    val friendId: String,
    val friendName: String,
    val prediction: String,
    val amount: Double,
    val currentRoundAmount: Double,
    val note: String?,
    val participation: String,
    val jackpotCatchUpAmount: Double
)
