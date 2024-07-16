package com.leeweeder.weighttracker.domain.usecases.log

import com.leeweeder.weighttracker.domain.model.Log
import com.leeweeder.weighttracker.domain.repository.LogRepository
import kotlinx.coroutines.flow.Flow

class GetLatestLogs(
    private val repository: LogRepository
) {
    operator fun invoke(number: Int = Default.LATEST_LOG_NUMBER): Flow<List<Log>> {
        return repository.getLatestLogs(number)
    }
}

private object Default {
    const val LATEST_LOG_NUMBER = 1
}