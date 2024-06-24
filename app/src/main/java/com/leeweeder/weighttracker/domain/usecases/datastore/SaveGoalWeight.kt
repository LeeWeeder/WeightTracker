package com.leeweeder.weighttracker.domain.usecases.datastore

import com.leeweeder.weighttracker.domain.repository.DataStoreRepository

class SaveGoalWeight(
    private val repository: DataStoreRepository
) {
    suspend operator fun invoke(value: Float) {
        repository.saveGoalWeight(value)
    }
}