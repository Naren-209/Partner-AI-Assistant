package com.partner.assistant.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.partner.assistant.data.PartnerDatabase
import com.partner.assistant.ui.components.BottomNavBar
import com.partner.assistant.ui.screens.HomeScreen
import com.partner.assistant.ui.screens.NotesScreen
import com.partner.assistant.ui.screens.SettingsScreen
import com.partner.assistant.ui.screens.ToolsScreen
import com.partner.assistant.ui.state.ListeningStatus

object Routes {
    const val HOME = "home"
    const val NOTES = "notes"
    const val TOOLS = "tools"
    const val SETTINGS = "settings"
}

@Composable
fun PartnerNavHost(database: PartnerDatabase) {
    val navController = rememberNavController()
    val listeningStatus by remember { mutableStateOf(ListeningStatus.OFF) }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Routes.HOME

    Scaffold(
        bottomBar = {
            BottomNavBar(currentRoute = currentRoute) { route ->
                navController.navigate(route) {
                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Routes.HOME,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Routes.HOME) {
                HomeScreen(
                    database = database,
                    listeningStatus = listeningStatus,
                    onOpenSettings = { navController.navigate(Routes.SETTINGS) }
                )
            }
            composable(Routes.NOTES) {
                NotesScreen(database = database)
            }
            composable(Routes.TOOLS) {
                ToolsScreen()
            }
            composable(Routes.SETTINGS) {
                SettingsScreen(
                    database = database,
                    onBack = { navController.navigate(Routes.HOME) }
                )
            }
        }
    }
}