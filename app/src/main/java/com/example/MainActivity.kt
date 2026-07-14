package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.ui.chat.ChatViewModel
import com.example.ui.navigation.AppNavigation
import com.example.ui.settings.SettingsViewModel
import com.example.ui.theme.JarvisTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val appContainer = (application as JarvisApplication).container

        setContent {
            val settingsViewModel: SettingsViewModel = viewModel(
                factory = SettingsViewModel.provideFactory(appContainer.settingsRepository)
            )
            val chatViewModel: ChatViewModel = viewModel(
                factory = ChatViewModel.provideFactory(appContainer.chatRepository)
            )

            val isDarkMode by settingsViewModel.isDarkMode.collectAsStateWithLifecycle(initialValue = isSystemInDarkTheme())
            val navController = rememberNavController()

            JarvisTheme(darkTheme = isDarkMode) {
                AppNavigation(
                    navController = navController,
                    chatViewModel = chatViewModel,
                    settingsViewModel = settingsViewModel
                )
            }
        }
    }
}
