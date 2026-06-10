package com.example.healthylife

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.healthylife.navigation.AppNavigation
import com.example.healthylife.ui.theme.HealthyLifeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HealthyLifeTheme(darkTheme = true) {
                AppNavigation()
            }
        }
    }
}