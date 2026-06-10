package com.example.healthylife.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Exercise : Screen("exercise")
    object Nutrition : Screen("nutrition")
    object Sleep : Screen("sleep")
    object Progress : Screen("progress")
    object Profile : Screen("profile")
}