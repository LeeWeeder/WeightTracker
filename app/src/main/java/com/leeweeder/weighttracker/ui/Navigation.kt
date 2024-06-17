/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.leeweeder.weighttracker.ui

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.IntOffset
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.leeweeder.weighttracker.ui.add_edit_log.AddEditLogScreen
import com.leeweeder.weighttracker.ui.add_edit_log.FROM_SET_GOAL_WEIGHT_SCREEN
import com.leeweeder.weighttracker.ui.add_edit_log.LOG_ID_KEY
import com.leeweeder.weighttracker.ui.home.HomeScreen
import com.leeweeder.weighttracker.ui.log.LogScreen
import com.leeweeder.weighttracker.ui.onboarding.OnBoardingScreen
import com.leeweeder.weighttracker.util.Screen

val LocalNavController =
    compositionLocalOf<NavHostController> { error("NavHostController error") }

@Composable
fun MainNavigation(
    viewModel: MainActivityViewModel
) {
    val navController = rememberNavController()

    val (durationMillis, easing) = Pair(350, EaseInOut)

    val enterTransition: @JvmSuppressWildcards (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?) =
        {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = tween(durationMillis = durationMillis, easing = easing)
            )
        }

    val exitTransition: @JvmSuppressWildcards (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?) =
        {
            fadeOut(
                targetAlpha = 0.5f,
                animationSpec = tween(durationMillis = durationMillis, easing = easing)
            ) + slideOut(animationSpec = tween(durationMillis = durationMillis, easing = easing)) {
                IntOffset(-150, 0)
            }
        }

    val popExitTransition: @JvmSuppressWildcards (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?) =
        {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.End,
                animationSpec = tween(durationMillis = durationMillis, easing = easing)
            )
        }

    val popEnterTransition: @JvmSuppressWildcards (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?) =
        {
            fadeIn(
                initialAlpha = 0.5f,
                animationSpec = tween(durationMillis = durationMillis, easing = easing)
            ) + slideIn(animationSpec = tween(durationMillis = durationMillis, easing = easing)) {
                IntOffset(-150, 0)
            }
        }

    CompositionLocalProvider(value = LocalNavController provides navController) {
        val addEditLogSharedViewModel: AddEditLogSharedViewModel = viewModel()
        // Start destination to OnBoarding for testing purposes
        NavHost(navController = navController, startDestination = Screen.OnBoardingScreen.route) {
            composable(
                Screen.HomeScreen.route,
                enterTransition = enterTransition,
                exitTransition = exitTransition,
                popExitTransition = popExitTransition,
                popEnterTransition = popEnterTransition
            ) {
                HomeScreen()
            }
            composable(
                Screen.LogScreen.route,
                enterTransition = enterTransition,
                exitTransition = exitTransition,
                popExitTransition = popExitTransition,
                popEnterTransition = popEnterTransition
            ) { LogScreen(sharedViewModel = addEditLogSharedViewModel) }
            composable(
                route = Screen.AddEditLogScreen.route,
                arguments = listOf(
                    navArgument(name = LOG_ID_KEY) {
                        type = NavType.IntType
                        defaultValue = -1
                    },
                    navArgument(name = FROM_SET_GOAL_WEIGHT_SCREEN) {
                        type = NavType.BoolType
                        defaultValue = false
                    }
                ),
                enterTransition = enterTransition,
                exitTransition = exitTransition,
                popExitTransition = popExitTransition,
                popEnterTransition = popEnterTransition
            ) {
                AddEditLogScreen(sharedViewModel = addEditLogSharedViewModel)
            }
            composable(
                route = Screen.OnBoardingScreen.route
            ) {
                OnBoardingScreen()
            }
        }
    }
}
