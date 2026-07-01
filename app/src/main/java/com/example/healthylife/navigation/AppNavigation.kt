package com.example.healthylife.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.*
import com.example.healthylife.data.HealthRepository
import com.example.healthylife.ui.screens.*
import com.example.healthylife.ui.theme.*

data class NavItem(
    val route: String,
    val icon: ImageVector,
    val title: String
)

@Composable
fun AppNavigation(repository: HealthRepository) {

    val navController = rememberNavController()

    val items = listOf(
        NavItem("home",      Icons.Default.Home,        "Beranda"),
        NavItem("exercise",  Icons.Default.FitnessCenter,"Olahraga"),
        NavItem("nutrition", Icons.Default.Restaurant,   "Makanan"),
        NavItem("sleep",     Icons.Default.Bedtime,      "Tidur"),
        NavItem("profile",   Icons.Default.Person,       "Profil")
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            NavigationBar(
                containerColor = NavyDark,
                contentColor   = HealthGreen
            ) {
                val currentRoute = navController
                    .currentBackStackEntryAsState()
                    .value?.destination?.route

                items.forEach { item ->
                    NavigationBarItem(
                        selected = currentRoute == item.route,
                        onClick  = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState    = true
                            }
                        },
                        icon = {
                            Icon(item.icon, contentDescription = item.title)
                        },
                        label = {
                            Text(item.title)
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor   = HealthGreen,
                            selectedTextColor   = HealthGreen,
                            indicatorColor      = HealthGreen.copy(alpha = 0.12f),
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController    = navController,
            startDestination = "home"
        ) {
            composable("home")      { HomeScreen(innerPadding, repository) }
            composable("exercise")  { ExerciseScreen(innerPadding, repository) }
            composable("nutrition") { NutritionScreen(innerPadding, repository) }
            composable("sleep")     { SleepScreen(innerPadding, repository) }
            composable("profile")   {
                ProfileScreen(
                    innerPadding,
                    repository,
                    onOpenSettings = { navController.navigate("settings") }
                )
            }
            composable("settings")  {
                SettingsScreen(
                    innerPadding,
                    repository,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}