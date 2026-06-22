package com.example.wmfunbett2026.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tipp_groups",
    foreignKeys = [
        ForeignKey(
            entity = MatchEntity::class,
            parentColumns = ["id"],
            childColumns = ["matchId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("matchId")]
)
data class TippGroupEntity(
    @PrimaryKey val id: String,
    val matchId: String,
    val title: String,
    val timeScope: String,
    val entryAmount: Double?,
    val note: String?
)
