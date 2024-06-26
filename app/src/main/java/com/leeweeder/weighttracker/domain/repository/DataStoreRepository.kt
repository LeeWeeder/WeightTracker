package com.leeweeder.weighttracker.domain.repository

import kotlinx.coroutines.flow.Flow

interface DataStoreRepository {
    suspend fun saveGoalWeight(value: Int)
    fun readGoalWeightState(): Flow<Int>

    suspend fun saveShouldHideOnBoarding(shouldHideOnBoarding: Boolean)
    fun readOnBoardingState(): Flow<Boolean>
}