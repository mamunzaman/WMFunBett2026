package com.example.wmfunbett2026

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.wmfunbett2026.ui.navigation.AppScreen
import com.example.wmfunbett2026.ui.navigation.RoundsNavGraph
import com.example.wmfunbett2026.ui.navigation.RoundsRoutes
import com.example.wmfunbett2026.ui.screens.DashboardScreen
import com.example.wmfunbett2026.ui.screens.JackpotScreen
import com.example.wmfunbett2026.ui.screens.SettingsScreen
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
    var selectedScreen by rememberSaveable { mutableStateOf(AppScreen.Dashboard) }
    val roundsNavController = rememberNavController()
    val roundsBackStackEntry by roundsNavController.currentBackStackEntryAsState()
    val showBottomBar = selectedScreen != AppScreen.Rounds ||
        roundsBackStackEntry?.destination?.route == RoundsRoutes.ROUNDS_LIST

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = DarkNavy,
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = SurfaceDark,
                    tonalElevation = 0.dp
                ) {
                    AppScreen.entries.forEach { screen ->
                        val selected = selectedScreen == screen
                        NavigationBarItem(
                            selected = selected,
                            onClick = { selectedScreen = screen },
                            icon = {
                                Icon(
                                    imageVector = screen.icon,
                                    contentDescription = screen.label
                                )
                            },
                            label = {
                                Text(
                                    text = screen.label,
                                    style = MaterialTheme.typography.labelMedium
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = JackpotGold,
                                selectedTextColor = JackpotGold,
                                unselectedIconColor = SecondaryText,
                                unselectedTextColor = SecondaryText,
                                indicatorColor = PrimaryBlue.copy(alpha = 0.25f)
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        when (selectedScreen) {
            AppScreen.Dashboard -> DashboardScreen(Modifier.padding(innerPadding))
            AppScreen.Rounds -> RoundsNavGraph(
                navController = roundsNavController,
                modifier = Modifier.padding(innerPadding)
            )
            AppScreen.Jackpot -> JackpotScreen(Modifier.padding(innerPadding))
            AppScreen.Settings -> SettingsScreen(Modifier.padding(innerPadding))
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
