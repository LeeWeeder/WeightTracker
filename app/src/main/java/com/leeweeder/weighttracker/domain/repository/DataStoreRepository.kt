package com.leeweeder.weighttracker.domain.repository

import com.leeweeder.weighttracker.StartingWeight
import com.leeweeder.weighttracker.domain.model.Log
import kotlinx.coroutines.flow.Flow

interface DataStoreRepository {
    val startingWeightFlow: Flow<StartingWeight>
    suspend fun saveGoalWeight(value: Int)
    fun readGoalWeightState(): Flow<Int>

    suspend fun saveShouldHideOnBoarding(shouldHideOnBoarding: Boolean)
    fun readOnBoardingState(): Flow<Boolean>

    suspend fun saveStartingWeight(log: Log)
}