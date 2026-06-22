package com.example.wmfunbett2026.data.local

import com.example.wmfunbett2026.data.local.entity.EntryEntity
import com.example.wmfunbett2026.data.local.entity.FriendEntity
import com.example.wmfunbett2026.data.local.entity.LeagueEntity
import com.example.wmfunbett2026.data.local.entity.MatchEntity
import com.example.wmfunbett2026.data.local.entity.TippGroupEntity
import com.example.wmfunbett2026.data.model.Day
import com.example.wmfunbett2026.data.model.Entry
import com.example.wmfunbett2026.data.model.Friend
import com.example.wmfunbett2026.data.model.Game
import com.example.wmfunbett2026.data.model.MatchStatus
import com.example.wmfunbett2026.data.model.Round
import com.example.wmfunbett2026.data.model.TimeScope
import com.example.wmfunbett2026.data.model.TippGroup

fun Round.toLeagueEntity(): LeagueEntity = LeagueEntity(
    id = id,
    name = name,
    note = note
)

fun LeagueEntity.toRound(
    matches: List<MatchEntity>,
    tippGroups: List<TippGroupEntity>,
    entries: List<EntryEntity>
): Round {
    val groupsByMatch = tippGroups.groupBy { it.matchId }
    val entriesByGroup = entries.groupBy { it.tippGroupId }
    val leagueMatches = matches.filter { it.leagueId == id }
    val days = leagueMatches
        .groupBy { it.dayId }
        .map { (dayId, dayMatches) ->
            Day(
                id = dayId,
                name = dayMatches.first().dayLabel,
                games = dayMatches.map { match ->
                    match.toGame(
                        groupsByMatch[match.id].orEmpty(),
                        entriesByGroup
                    )
                }
            )
        }
        .sortedBy { it.name }
    return Round(
        id = id,
        name = name,
        note = note,
        days = days
    )
}

fun Game.toMatchEntity(leagueId: String, dayId: String, dayLabel: String): MatchEntity =
    MatchEntity(
        id = id,
        leagueId = leagueId,
        dayId = dayId,
        dayLabel = dayLabel,
        teamA = teamA,
        teamB = teamB,
        dateTimeLabel = dateTimeLabel,
        teamAScore = teamAScore,
        teamBScore = teamBScore,
        status = status.name,
        note = note
    )

fun MatchEntity.toGame(
    groupEntities: List<TippGroupEntity>,
    entriesByGroup: Map<String, List<EntryEntity>>
): Game = Game(
    id = id,
    teamA = teamA,
    teamB = teamB,
    dateTimeLabel = dateTimeLabel,
    teamAScore = teamAScore,
    teamBScore = teamBScore,
    status = runCatching { MatchStatus.valueOf(status) }.getOrDefault(MatchStatus.NOT_STARTED),
    tippGroups = groupEntities.map { it.toTippGroup(entriesByGroup[it.id].orEmpty()) },
    note = note
)

fun TippGroup.toEntity(matchId: String): TippGroupEntity = TippGroupEntity(
    id = id,
    matchId = matchId,
    title = title,
    timeScope = timeScope.name,
    entryAmount = entryAmount,
    note = note
)

fun TippGroupEntity.toTippGroup(entryEntities: List<EntryEntity>): TippGroup = TippGroup(
    id = id,
    title = title,
    timeScope = runCatching { TimeScope.valueOf(timeScope) }.getOrDefault(TimeScope.FULL_TIME),
    entries = entryEntities.map { it.toEntry() },
    entryAmount = entryAmount,
    note = note
)

fun Entry.toEntity(tippGroupId: String): EntryEntity = EntryEntity(
    id = id,
    tippGroupId = tippGroupId,
    friendId = friendId,
    friendName = friendName,
    prediction = prediction,
    amount = amount,
    currentRoundAmount = currentRoundAmount,
    note = note
)

fun EntryEntity.toEntry(): Entry = Entry(
    id = id,
    friendId = friendId,
    friendName = friendName,
    prediction = prediction,
    amount = amount,
    currentRoundAmount = currentRoundAmount,
    note = note
)

fun Friend.toEntity(): FriendEntity = FriendEntity(
    id = id,
    name = name,
    note = note,
    createdAt = createdAt
)

fun FriendEntity.toFriend(): Friend = Friend(
    id = id,
    name = name,
    note = note,
    createdAt = createdAt
)
