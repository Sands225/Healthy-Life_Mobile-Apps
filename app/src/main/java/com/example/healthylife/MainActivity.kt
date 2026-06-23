package com.example.healthylife

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import com.example.healthylife.navigation.AppNavigation
import com.example.healthylife.ui.theme.HealthyLifeTheme
import com.example.healthylife.ui.theme.LocalDarkTheme
import com.example.healthylife.ui.theme.LocalThemeToggle

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var isDarkTheme by rememberSaveable { mutableStateOf(true) }
            
            CompositionLocalProvider(
                LocalDarkTheme provides isDarkTheme,
                LocalThemeToggle provides { isDarkTheme = !isDarkTheme }
            ) {
                HealthyLifeTheme(darkTheme = isDarkTheme) {
                    AppNavigation()
                }
            }
        }
    }
}