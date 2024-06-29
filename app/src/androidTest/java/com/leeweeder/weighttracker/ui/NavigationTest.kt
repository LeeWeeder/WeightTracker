package com.leeweeder.weighttracker.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.leeweeder.weighttracker.di.AppModule
import com.leeweeder.weighttracker.domain.model.Log
import com.leeweeder.weighttracker.ui.log.LogScreen
import com.leeweeder.weighttracker.ui.log.LogUiState
import com.leeweeder.weighttracker.ui.theme.WeightTrackerTheme
import com.leeweeder.weighttracker.util.Screen
import com.leeweeder.weighttracker.util.Weight
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Before
import org.junit.Rule
import java.time.LocalDate

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
                        val log = listOf(
                            Log(
                                id = 0,
                                date = LocalDate.now(),
                                weight = Weight(0f)
                            )
                        )
                        LogScreen(uiState = LogUiState(log), newlyAddedId = null, onEvent = {}, onEmphasisAnimationFinished = {})
                    }
                }
            }
        }
    }
}