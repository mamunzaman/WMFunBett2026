package com.example.wmfunbett2026.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "matches",
    foreignKeys = [
        ForeignKey(
            entity = LeagueEntity::class,
            parentColumns = ["id"],
            childColumns = ["leagueId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("leagueId"), Index("dayId")]
)
data class MatchEntity(
    @PrimaryKey val id: String,
    val leagueId: String,
    val dayId: String,
    val dayLabel: String,
    val teamA: String,
    val teamB: String,
    val dateTimeLabel: String,
    val teamAScore: Int?,
    val teamBScore: Int?,
    val status: String,
    val note: String?
)
