package com.example.wmfunbett2026

import android.app.Application
import com.example.wmfunbett2026.data.repository.FunBettRepository

class WMFunBettApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FunBettRepository.initialize(this)
    }
}
