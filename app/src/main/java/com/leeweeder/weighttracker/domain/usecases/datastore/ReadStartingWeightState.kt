package com.leeweeder.weighttracker.domain.usecases.datastore

import com.leeweeder.weighttracker.StartingWeight
import com.leeweeder.weighttracker.domain.repository.DataStoreRepository
import kotlinx.coroutines.flow.Flow

class ReadStartingWeightState(
    private val repository: DataStoreRepository
) {
    operator fun invoke(): Flow<StartingWeight> {
        return repository.startingWeightFlow
    }
}