package com.leeweeder.weighttracker.ui

import com.leeweeder.weighttracker.di.AppModule
import com.leeweeder.weighttracker.ui.log.LogScreen
import com.leeweeder.weighttracker.ui.theme.WeightTrackerTheme
import com.leeweeder.weighttracker.util.Screen
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.leeweeder.weighttracker.ui.log.LogUiState
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Before
import org.junit.Rule

@HiltAndroidTest
@UninstallModules(AppModule::class)
class LogScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @ExperimentalAnimationApi
    @Before
    fun setUp() {
        hiltRule.inject()
        composeRule.setContent {
            val navController = rememberNavController()
            WeightTrackerTheme {
                NavHost(
                    navController = navController,
                    startDestination = Screen.LogScreen.route
                ) {
                    composable(route = Screen.LogScreen.route) {
                        LogScreen(uiState = LogUiState(log))
                    }
                }
            }
        }
    }
}