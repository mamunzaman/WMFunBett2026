package com.example.wmfunbett2026.data.local

import com.example.wmfunbett2026.data.local.dao.EntryDao
import com.example.wmfunbett2026.data.local.dao.FriendDao
import com.example.wmfunbett2026.data.local.dao.LeagueDao
import com.example.wmfunbett2026.data.local.dao.MatchDao
import com.example.wmfunbett2026.data.local.dao.TippGroupDao
import com.example.wmfunbett2026.data.model.Day
import com.example.wmfunbett2026.data.model.Entry
import com.example.wmfunbett2026.data.model.Friend
import com.example.wmfunbett2026.data.model.Game
import com.example.wmfunbett2026.data.model.Round
import com.example.wmfunbett2026.data.model.TippGroup

class FunBettLocalStore(
    private val leagueDao: LeagueDao,
    private val matchDao: MatchDao,
    private val tippGroupDao: TippGroupDao,
    private val entryDao: EntryDao,
    private val friendDao: FriendDao
) {
    fun isEmpty(): Boolean = leagueDao.count() == 0

    fun loadFriends(): List<Friend> = friendDao.getAll().map { it.toFriend() }

    fun persistFriend(friend: Friend) {
        friendDao.insert(friend.toEntity())
    }

    fun deleteFriend(friendId: String) {
        friendDao.deleteById(friendId)
    }

    fun persistFriends(friends: List<Friend>) {
        friends.forEach { persistFriend(it) }
    }

    fun loadRounds(): List<Round> {
        val leagues = leagueDao.getAll()
        val matches = matchDao.getAll()
        val tippGroups = tippGroupDao.getAll()
        val entries = entryDao.getAll()
        return leagues.map { league ->
            league.toRound(matches, tippGroups, entries)
        }
    }

    fun persistAll(rounds: List<Round>) {
        rounds.forEach { round -> persistRound(round) }
    }

    fun persistRound(round: Round) {
        leagueDao.insert(round.toLeagueEntity())
        round.days.forEach { day ->
            day.games.forEach { game ->
                persistGame(round.id, day, game)
            }
        }
    }

    fun persistGame(leagueId: String, day: Day, game: Game) {
        matchDao.insert(game.toMatchEntity(leagueId, day.id, day.name))
        game.tippGroups.forEach { group ->
            persistTippGroup(game.id, group)
        }
    }

    fun persistTippGroup(matchId: String, group: TippGroup) {
        tippGroupDao.insert(group.toEntity(matchId))
        group.entries.forEach { entry ->
            persistEntry(group.id, entry)
        }
    }

    fun persistEntry(tippGroupId: String, entry: Entry) {
        entryDao.insert(entry.toEntity(tippGroupId))
    }

    fun updateGame(game: Game, leagueId: String, day: Day) {
        matchDao.update(game.toMatchEntity(leagueId, day.id, day.name))
    }

    fun deleteLeague(leagueId: String) {
        leagueDao.deleteById(leagueId)
    }

    fun deleteMatch(matchId: String) {
        matchDao.deleteById(matchId)
    }

    fun deleteTippGroup(tippGroupId: String) {
        tippGroupDao.deleteById(tippGroupId)
    }

    fun deleteEntry(tippGroupId: String, entryId: String) {
        entryDao.deleteById(tippGroupId, entryId)
    }
}
