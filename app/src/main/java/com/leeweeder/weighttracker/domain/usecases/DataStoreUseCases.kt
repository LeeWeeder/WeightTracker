package com.leeweeder.weighttracker.domain.usecases

import com.leeweeder.weighttracker.domain.usecases.datastore.ReadGoalWeightState
import com.leeweeder.weighttracker.domain.usecases.datastore.ReadOnBoardingState
import com.leeweeder.weighttracker.domain.usecases.datastore.SaveGoalWeight
import com.leeweeder.weighttracker.domain.usecases.datastore.SaveShouldHideOnBoarding

data class DataStoreUseCases(
    val saveGoalWeight: SaveGoalWeight,
    val readGoalWeightState: ReadGoalWeightState,
    val saveShouldHideOnBoarding: SaveShouldHideOnBoarding,
    val readOnBoardingState: ReadOnBoardingState
)
