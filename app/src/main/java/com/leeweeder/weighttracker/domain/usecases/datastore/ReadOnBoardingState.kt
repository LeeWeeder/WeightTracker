package com.leeweeder.weighttracker.domain.usecases.datastore

import com.leeweeder.weighttracker.domain.repository.DataStoreRepository
import kotlinx.coroutines.flow.Flow

class ReadOnBoardingState(
    private val repository: DataStoreRepository
) {
    operator fun invoke(): Flow<Boolean> {
        return repository.readOnBoardingState()
    }
}