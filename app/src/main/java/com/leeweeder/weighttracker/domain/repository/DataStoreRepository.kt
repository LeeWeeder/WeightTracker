package com.leeweeder.weighttracker.domain.repository

import kotlinx.coroutines.flow.Flow

interface DataStoreRepository {
    suspend fun saveGoalWeight(value: Double)
    fun readGoalWeightState(): Flow<Double>

    suspend fun saveOnBoardingState(shouldHideOnBoarding: Boolean)
    fun readOnBoardingState(): Flow<Boolean>
}