package com.leeweeder.weighttracker.domain.usecases.datastore

import com.leeweeder.weighttracker.domain.repository.DataStoreRepository
import com.leeweeder.weighttracker.util.StartingWeightModel

class SaveStartingWeight(
    private val repository: DataStoreRepository
) {
    suspend operator fun invoke(startingWeight: StartingWeightModel) {
        repository.saveStartingWeight(startingWeight)
    }
}