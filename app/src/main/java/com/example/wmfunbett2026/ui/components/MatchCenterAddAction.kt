package com.example.wmfunbett2026.ui.components

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object MatchCenterAddAction {
    var handler: (() -> Unit)? by mutableStateOf(null)

    fun invoke() {
        handler?.invoke()
    }
}
