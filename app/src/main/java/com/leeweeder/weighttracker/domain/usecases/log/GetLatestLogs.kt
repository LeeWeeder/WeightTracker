package com.leeweeder.weighttracker.domain.usecases.log

import com.leeweeder.weighttracker.domain.model.Log
import com.leeweeder.weighttracker.domain.repository.LogRepository
import kotlinx.coroutines.flow.Flow

class GetLatestLogs(
    private val repository: LogRepository
) {
    operator fun invoke(number: Int = 1): Flow<List<Log>> {
        return repository.getLatestLogs(number)
    }
}