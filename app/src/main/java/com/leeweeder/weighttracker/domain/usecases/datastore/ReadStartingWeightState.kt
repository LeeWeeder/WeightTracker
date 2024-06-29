package com.leeweeder.weighttracker.domain.usecases.datastore

import com.leeweeder.weighttracker.domain.model.Log
import com.leeweeder.weighttracker.domain.repository.DataStoreRepository
import com.leeweeder.weighttracker.util.Weight
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class ReadStartingWeightState(
    private val repository: DataStoreRepository
) {
    operator fun invoke(): Flow<Log> {
        return repository.startingWeightFlow.map { preferences ->
            Log(weight = Weight(preferences.weight), date = LocalDate.parse(preferences.date))
        }
    }
}