package com.leeweeder.weighttracker.domain.usecases

import com.leeweeder.weighttracker.domain.usecases.datastore.ReadGoalWeightState
import com.leeweeder.weighttracker.domain.usecases.datastore.ReadOnBoardingState
import com.leeweeder.weighttracker.domain.usecases.datastore.SaveGoalWeight
import com.leeweeder.weighttracker.domain.usecases.datastore.SaveOnBoardingState

data class DataStoreUseCases(
    val saveGoalWeight: SaveGoalWeight,
    val readGoalWeightState: ReadGoalWeightState,
    val saveOnBoardingState: SaveOnBoardingState,
    val readOnBoardingState: ReadOnBoardingState
)
