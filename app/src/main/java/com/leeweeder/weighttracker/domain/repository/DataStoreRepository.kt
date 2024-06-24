package com.leeweeder.weighttracker.domain.repository

import kotlinx.coroutines.flow.Flow

interface DataStoreRepository {
    suspend fun saveGoalWeight(value: Float)
    fun readGoalWeightState(): Flow<Float>

    suspend fun saveShouldHideOnBoarding(shouldHideOnBoarding: Boolean)
    fun readOnBoardingState(): Flow<Boolean>
}