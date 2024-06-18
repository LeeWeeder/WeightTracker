package com.leeweeder.weighttracker.util

import com.leeweeder.weighttracker.ui.add_edit_log.LOG_ID_KEY

const val FROM_ON_BOARDING_KEY = "fromOnBoarding"

sealed class Screen(val route: String) {
    data object LogScreen : Screen("log_screen")
    data object AddEditLogScreen :
        Screen("add_edit_log_screen?$LOG_ID_KEY={$LOG_ID_KEY}") {
            fun createRoute(logId: Int = -1): String {
                return route.substringBefore('?') + "?$LOG_ID_KEY=$logId"
            }
        }

    data object HomeScreen : Screen("home_screen?$FROM_ON_BOARDING_KEY={$FROM_ON_BOARDING_KEY}") {
        val fromOnBoardingRoute = route.substringBefore('?') + "?$FROM_ON_BOARDING_KEY=true"
    }
    data object OnBoardingScreen : Screen("onboarding_screen")
}