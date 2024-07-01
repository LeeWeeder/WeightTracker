package com.leeweeder.weighttracker.ui.onboarding

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.leeweeder.weighttracker.ui.LocalNavController
import com.leeweeder.weighttracker.ui.components.GoalScreen
import com.leeweeder.weighttracker.util.Screen

@Composable
fun OnBoardingScreen(
    viewmodel: OnBoardingViewModel = hiltViewModel()
) {
    OnBoardingScreen(
        onFinishOnBoarding = viewmodel::onFinishOnBoarding
    )
}

@Composable
fun OnBoardingScreen(
    onFinishOnBoarding: (weight: Int) -> Unit
) {
    val navController = LocalNavController.current
    GoalScreen { weight ->
        onFinishOnBoarding(weight)
        navController.navigate(Screen.HomeScreen.fromOnBoardingRoute) {
            popUpTo(Screen.OnBoardingScreen.route) {
                inclusive = true
            }
        }
    }
}