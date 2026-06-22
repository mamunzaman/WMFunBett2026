package com.example.wmfunbett2026

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.wmfunbett2026.data.repository.FunBettRepository
import com.example.wmfunbett2026.ui.components.AddMatchSheet
import com.example.wmfunbett2026.ui.components.AddTippGroupSheet
import com.example.wmfunbett2026.ui.components.CreateRoundSheet
import com.example.wmfunbett2026.ui.components.MatchCenterBottomNav
import com.example.wmfunbett2026.ui.components.ModalSheetBackdropOverlay
import com.example.wmfunbett2026.ui.components.ModalSheetBackdropState
import com.example.wmfunbett2026.ui.components.TippsCenterActionSheet
import com.example.wmfunbett2026.ui.components.modalSheetBackdropBlur
import com.example.wmfunbett2026.ui.navigation.AppScreen
import com.example.wmfunbett2026.ui.navigation.LeaguesNavGraph
import com.example.wmfunbett2026.ui.navigation.MatchesNavGraph
import com.example.wmfunbett2026.ui.navigation.resolveCreateNavigationState
import com.example.wmfunbett2026.ui.screens.SettingsScreen
import com.example.wmfunbett2026.ui.screens.friends.FriendsScreen
import com.example.wmfunbett2026.ui.theme.BackgroundDeep
import com.example.wmfunbett2026.ui.theme.WMFunBett2026Theme

import java.time.format.DateTimeFormatter
import java.util.Locale

private val AddMatchDateFormatter =
    DateTimeFormatter.ofPattern("EEE d MMM", Locale.ENGLISH)

private val AddMatchTimeFormatter =
    DateTimeFormatter.ofPattern("HH:mm", Locale.ENGLISH)

private sealed class CreateSheet {
    data object Menu : CreateSheet()
    data object Round : CreateSheet()
    data class Match(val lockedLeagueId: String?) : CreateSheet()
    data class TippGroup(val gameId: String) : CreateSheet()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.parseColor("#081220"))
        )
        setContent {
            WMFunBett2026Theme {
                AppShell()
            }
        }
    }
}

@Composable
fun AppShell(modifier: Modifier = Modifier) {
    var selectedScreen by rememberSaveable { mutableStateOf(AppScreen.Matches) }
    var activeCreateSheet by remember { mutableStateOf<CreateSheet?>(null) }
    val matchesNavController = rememberNavController()
    val leaguesNavController = rememberNavController()
    val sheetBackdropActive = ModalSheetBackdropState.isActive

    val matchesBackStackEntry by matchesNavController.currentBackStackEntryAsState()
    val leaguesBackStackEntry by leaguesNavController.currentBackStackEntryAsState()

    val createNavState = remember(
        selectedScreen,
        matchesBackStackEntry,
        leaguesBackStackEntry
    ) {
        val entry = when (selectedScreen) {
            AppScreen.Matches -> matchesBackStackEntry
            AppScreen.Leagues -> leaguesBackStackEntry
            else -> null
        }
        resolveCreateNavigationState(selectedScreen, entry)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDeep)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .modalSheetBackdropBlur(sheetBackdropActive)
        ) {
            when (selectedScreen) {
                AppScreen.Matches -> MatchesNavGraph(
                    navController = matchesNavController,
                    modifier = Modifier.fillMaxSize()
                )
                AppScreen.Leagues -> LeaguesNavGraph(
                    navController = leaguesNavController,
                    modifier = Modifier.fillMaxSize()
                )
                AppScreen.Friends -> FriendsScreen(Modifier.fillMaxSize())
                AppScreen.Settings -> SettingsScreen(Modifier.fillMaxSize())
            }
        }

        ModalSheetBackdropOverlay(active = sheetBackdropActive)

        MatchCenterBottomNav(
            modifier = Modifier.align(Alignment.BottomCenter),
            selectedScreen = selectedScreen,
            onScreenSelected = { selectedScreen = it },
            onCenterAddClick = { activeCreateSheet = CreateSheet.Menu }
        )
    }

    when (val sheet = activeCreateSheet) {
        CreateSheet.Menu -> {
            TippsCenterActionSheet(
                context = createNavState.context,
                onDismiss = { activeCreateSheet = null },
                onRoundClick = { activeCreateSheet = CreateSheet.Round },
                onMatchClick = {
                    activeCreateSheet = CreateSheet.Match(createNavState.preselectedLeagueId)
                },
                onTippGroupClick = {
                    createNavState.activeGameId?.let { gameId ->
                        activeCreateSheet = CreateSheet.TippGroup(gameId)
                    }
                },
                onEntryClick = {
                    activeCreateSheet = null
                    selectedScreen = AppScreen.Friends
                }
            )
        }
        CreateSheet.Round -> {
            CreateRoundSheet(
                onDismiss = { activeCreateSheet = null },
                onCreate = { name ->
                    FunBettRepository.addRound(name, null)
                    activeCreateSheet = null
                }
            )
        }
        is CreateSheet.Match -> {
            AddMatchSheet(
                lockedLeagueId = sheet.lockedLeagueId,
                onDismiss = { activeCreateSheet = null },
                onCreate = { leagueId, leagueName, teamA, teamB, day, time, note ->
                    if (!FunBettRepository.isMatchDateAllowed(day)) return@AddMatchSheet
                    val roundId = FunBettRepository.resolveRoundIdForLeague(leagueId, leagueName)
                        ?: return@AddMatchSheet
                    FunBettRepository.addGame(
                        roundId = roundId,
                        dayLabel = day.format(AddMatchDateFormatter),
                        teamA = teamA,
                        teamB = teamB,
                        dateLabel = day.format(AddMatchDateFormatter),
                        timeLabel = time.format(AddMatchTimeFormatter),
                        note = note
                    )
                    activeCreateSheet = null
                }
            )
        }
        is CreateSheet.TippGroup -> {
            AddTippGroupSheet(
                gameId = sheet.gameId,
                onDismiss = { activeCreateSheet = null },
                onCreate = { tippType, entryAmount, note ->
                    FunBettRepository.addTippGroup(
                        gameId = sheet.gameId,
                        tippType = tippType,
                        entryAmount = entryAmount,
                        note = note
                    )
                    activeCreateSheet = null
                }
            )
        }
        null -> Unit
    }
}
