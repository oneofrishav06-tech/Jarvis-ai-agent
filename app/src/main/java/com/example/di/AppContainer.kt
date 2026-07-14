package com.example.di

import android.content.Context
import com.example.data.local.AppDatabase
import com.example.data.repository.ChatRepository
import com.example.data.repository.SettingsRepository

class AppContainer(private val context: Context) {
    val database: AppDatabase by lazy { AppDatabase.getDatabase(context) }
    
    val settingsRepository: SettingsRepository by lazy {
        SettingsRepository(context)
    }

    val chatRepository: ChatRepository by lazy {
        ChatRepository(database.chatDao(), settingsRepository)
    }
}
