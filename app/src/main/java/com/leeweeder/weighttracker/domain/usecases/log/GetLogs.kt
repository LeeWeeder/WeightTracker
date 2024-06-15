package com.leeweeder.weighttracker.domain.usecases.log

import com.leeweeder.weighttracker.domain.model.Log
import com.leeweeder.weighttracker.domain.repository.LogRepository
import kotlinx.coroutines.flow.Flow

class GetLogs(
    private val repository: LogRepository
) {
    operator fun invoke(): Flow<List<Log>> {
        return repository.getLogs()
    }
}