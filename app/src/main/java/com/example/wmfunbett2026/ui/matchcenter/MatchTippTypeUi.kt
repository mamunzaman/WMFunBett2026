package com.example.wmfunbett2026.ui.matchcenter

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.wmfunbett2026.R
import com.example.wmfunbett2026.data.model.MatchTippType

@StringRes
fun MatchTippType.labelRes(): Int = when (this) {
    MatchTippType.HALF_TIME -> R.string.tipp_type_half_time
    MatchTippType.SECOND_HALF -> R.string.tipp_type_second_half
    MatchTippType.FULL_TIME -> R.string.tipp_type_full_time
    MatchTippType.FULL_TIME_PENALTIES -> R.string.tipp_type_full_time_penalties
}

@Composable
fun MatchTippType.localizedLabel(): String = stringResource(labelRes())
