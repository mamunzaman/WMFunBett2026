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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.wmfunbett2026.ui.components.MatchCenterAddAction
import com.example.wmfunbett2026.ui.components.MatchCenterBottomNav
import com.example.wmfunbett2026.ui.components.ModalSheetBackdropOverlay
import com.example.wmfunbett2026.ui.components.ModalSheetBackdropState
import com.example.wmfunbett2026.ui.components.modalSheetBackdropBlur
import com.example.wmfunbett2026.ui.navigation.AppScreen
import com.example.wmfunbett2026.ui.navigation.TournamentNavGraph
import com.example.wmfunbett2026.ui.navigation.TournamentRoutes
import com.example.wmfunbett2026.ui.screens.HomeScreen
import com.example.wmfunbett2026.ui.screens.SettingsScreen
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
    var selectedScreen by rememberSaveable { mutableStateOf(AppScreen.WM2026) }
    val tournamentNavController = rememberNavController()
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
                AppScreen.Home -> HomeScreen(Modifier.fillMaxSize())
                AppScreen.WM2026 -> TournamentNavGraph(
                    navController = tournamentNavController,
                    modifier = Modifier.fillMaxSize()
                )
                AppScreen.Settings -> SettingsScreen(Modifier.fillMaxSize())
            }
        }

        ModalSheetBackdropOverlay(active = sheetBackdropActive)

        MatchCenterBottomNav(
            modifier = Modifier.align(Alignment.BottomCenter),
            selectedScreen = selectedScreen,
            onScreenSelected = { selectedScreen = it },
            onCenterAddClick = {
                selectedScreen = AppScreen.WM2026
                val handler = MatchCenterAddAction.handler
                if (handler != null) {
                    handler.invoke()
                } else {
                    tournamentNavController.navigate(TournamentRoutes.TOURNAMENT_LIST) {
                        launchSingleTop = true
                    }
                }
            }
        )
    }
}
