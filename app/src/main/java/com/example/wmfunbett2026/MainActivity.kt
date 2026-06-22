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
import androidx.navigation.compose.rememberNavController
import com.example.wmfunbett2026.data.repository.FunBettRepository
import com.example.wmfunbett2026.ui.components.CreateRoundSheet
import com.example.wmfunbett2026.ui.components.MatchCenterBottomNav
import com.example.wmfunbett2026.ui.components.ModalSheetBackdropOverlay
import com.example.wmfunbett2026.ui.components.ModalSheetBackdropState
import com.example.wmfunbett2026.ui.components.TippsCenterActionSheet
import com.example.wmfunbett2026.ui.components.modalSheetBackdropBlur
import com.example.wmfunbett2026.ui.navigation.AppScreen
import com.example.wmfunbett2026.ui.navigation.LeaguesNavGraph
import com.example.wmfunbett2026.ui.navigation.MatchesNavGraph
import com.example.wmfunbett2026.ui.screens.SettingsScreen
import com.example.wmfunbett2026.ui.screens.friends.FriendsScreen
import com.example.wmfunbett2026.ui.theme.BackgroundDeep
import com.example.wmfunbett2026.ui.theme.WMFunBett2026Theme

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
    var showTippsCenterSheet by remember { mutableStateOf(false) }
    var showCreateRoundSheet by remember { mutableStateOf(false) }
    val matchesNavController = rememberNavController()
    val leaguesNavController = rememberNavController()
    val sheetBackdropActive = ModalSheetBackdropState.isActive

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
            onCenterAddClick = { showTippsCenterSheet = true }
        )
    }

    if (showTippsCenterSheet) {
        TippsCenterActionSheet(
            onDismiss = { showTippsCenterSheet = false },
            onRoundClick = {
                showTippsCenterSheet = false
                showCreateRoundSheet = true
            }
        )
    }

    if (showCreateRoundSheet) {
        CreateRoundSheet(
            onDismiss = { showCreateRoundSheet = false },
            onCreate = { name ->
                FunBettRepository.addRound(name, null)
                showCreateRoundSheet = false
            }
        )
    }
}
