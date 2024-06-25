package com.leeweeder.weighttracker.domain.usecases.log

import com.leeweeder.weighttracker.domain.repository.LogRepository
import com.leeweeder.weighttracker.util.Weight
import kotlinx.coroutines.flow.Flow

class GetOldestLogWeight(
    private val repository: LogRepository
) {
    operator fun invoke(): Flow<Weight?> {
        return repository.getOldestLogWeight()
    }
}