package com.example.wmfunbett2026

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.example.wmfunbett2026.R
import com.example.wmfunbett2026.ui.navigation.AppScreen
import com.example.wmfunbett2026.ui.navigation.TournamentNavGraph
import com.example.wmfunbett2026.ui.screens.HomeScreen
import com.example.wmfunbett2026.ui.screens.KasseScreen
import com.example.wmfunbett2026.ui.theme.DarkNavy
import com.example.wmfunbett2026.ui.theme.JackpotGold
import com.example.wmfunbett2026.ui.theme.PrimaryBlue
import com.example.wmfunbett2026.ui.theme.SecondaryText
import com.example.wmfunbett2026.ui.theme.SurfaceDark
import com.example.wmfunbett2026.ui.theme.WMFunBett2026Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WMFunBett2026Theme {
                AppShell()
            }
        }
    }
}

@Composable
fun AppShell(modifier: Modifier = Modifier) {
    var selectedScreen by rememberSaveable { mutableStateOf(AppScreen.Home) }
    val tournamentNavController = rememberNavController()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = DarkNavy,
        bottomBar = {
            AppFooterNavigation(
                selectedScreen = selectedScreen,
                onScreenSelected = { selectedScreen = it }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedScreen) {
                AppScreen.Home -> HomeScreen(Modifier.fillMaxSize())
                AppScreen.WM2026 -> TournamentNavGraph(
                    navController = tournamentNavController,
                    modifier = Modifier.fillMaxSize()
                )
                AppScreen.Kasse -> KasseScreen(Modifier.fillMaxSize())
            }
        }
    }
}

@Composable
private fun AppFooterNavigation(
    selectedScreen: AppScreen,
    onScreenSelected: (AppScreen) -> Unit
) {
    NavigationBar(
        modifier = Modifier.fillMaxWidth(),
        containerColor = SurfaceDark,
        tonalElevation = 0.dp
    ) {
        AppScreen.entries.forEach { screen ->
            val selected = selectedScreen == screen
            val selectedColor = when {
                selected && screen == AppScreen.Kasse -> JackpotGold
                selected -> PrimaryBlue
                else -> SecondaryText
            }
            NavigationBarItem(
                selected = selected,
                onClick = { onScreenSelected(screen) },
                icon = {
                    Icon(
                        imageVector = screen.icon,
                        contentDescription = stringResource(screen.labelRes)
                    )
                },
                label = {
                    Text(
                        text = stringResource(screen.labelRes),
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = selectedColor,
                    selectedTextColor = selectedColor,
                    unselectedIconColor = SecondaryText,
                    unselectedTextColor = SecondaryText,
                    indicatorColor = if (screen == AppScreen.Kasse) {
                        JackpotGold.copy(alpha = 0.18f)
                    } else {
                        PrimaryBlue.copy(alpha = 0.22f)
                    },
                    disabledIconColor = SecondaryText,
                    disabledTextColor = SecondaryText
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppShellPreview() {
    WMFunBett2026Theme {
        AppShell()
    }
}
