package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.navigation.MomOSApp
import com.example.ui.theme.MomOSTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as? MomOSApplication ?: (applicationContext as? MomOSApplication)
        if (app == null) return

        setContent {
            val userProfile by app.userRepository.userProfile.collectAsStateWithLifecycle(initialValue = null)
            val isDark = userProfile?.isDarkMode ?: isSystemInDarkTheme()

            MomOSTheme(darkTheme = isDark) {
                MomOSApp(app = app)
            }
        }
    }
}
