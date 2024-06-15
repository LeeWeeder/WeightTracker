package com.leeweeder.weighttracker.util

sealed class Screen(val route: String) {
    data object LogScreen: Screen("log_screen")
    data object AddEditLogScreen: Screen("add_edit_log_screen")
    data object HomeScreen: Screen("home_screen")
}