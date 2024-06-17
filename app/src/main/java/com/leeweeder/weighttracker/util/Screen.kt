package com.leeweeder.weighttracker.util

import com.leeweeder.weighttracker.ui.add_edit_log.FROM_SET_GOAL_WEIGHT_SCREEN
import com.leeweeder.weighttracker.ui.add_edit_log.LOG_ID_KEY

sealed class Screen(val route: String) {
    data object LogScreen : Screen("log_screen")
    data object AddEditLogScreen :
        Screen("add_edit_log_screen?$LOG_ID_KEY={$LOG_ID_KEY}&$FROM_SET_GOAL_WEIGHT_SCREEN={$FROM_SET_GOAL_WEIGHT_SCREEN}") {
            fun createRoute(logId: Int = -1, fromSetGoalWeightScreen: Boolean = false): String {
                return route.substringBefore('?') + "?$LOG_ID_KEY=$logId&$FROM_SET_GOAL_WEIGHT_SCREEN=$fromSetGoalWeightScreen"
            }
        }

    data object HomeScreen : Screen("home_screen")
    data object OnBoardingScreen : Screen("onboarding_screen")
}