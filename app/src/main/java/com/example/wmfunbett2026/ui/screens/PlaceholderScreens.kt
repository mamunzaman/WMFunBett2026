package com.example.wmfunbett2026.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.wmfunbett2026.R
import com.example.wmfunbett2026.ui.components.MatchCenterEmptyState
import com.example.wmfunbett2026.ui.components.MatchCenterHeader
import com.example.wmfunbett2026.ui.theme.DarkNavy

@Composable
fun HistoryScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkNavy)
    ) {
        androidx.compose.foundation.layout.Column(modifier = Modifier.fillMaxSize()) {
            MatchCenterHeader(title = stringResource(R.string.nav_history))
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                MatchCenterEmptyState(
                    title = stringResource(R.string.history_empty_title),
                    message = stringResource(R.string.history_empty_message)
                )
            }
        }
    }
}

@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkNavy)
    ) {
        androidx.compose.foundation.layout.Column(modifier = Modifier.fillMaxSize()) {
            MatchCenterHeader(
                title = stringResource(R.string.nav_settings),
                showSearchIcon = true
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                MatchCenterEmptyState(
                    title = stringResource(R.string.settings_empty_title),
                    message = stringResource(R.string.settings_empty_message)
                )
            }
        }
    }
}
