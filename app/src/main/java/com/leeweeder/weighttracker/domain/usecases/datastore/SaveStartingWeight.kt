package com.leeweeder.weighttracker.domain.usecases.datastore

import com.leeweeder.weighttracker.domain.model.Log
import com.leeweeder.weighttracker.domain.repository.DataStoreRepository

class SaveStartingWeight(
    private val repository: DataStoreRepository
) {
    suspend operator fun invoke(log: Log) {
        repository.saveStartingWeight(log)
    }
}