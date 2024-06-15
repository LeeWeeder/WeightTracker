package com.leeweeder.weighttracker.domain.usecases.log

import com.leeweeder.weighttracker.domain.model.Log
import com.leeweeder.weighttracker.domain.repository.LogRepository

class GetLogById(
    private val repository: LogRepository
) {
    suspend operator fun invoke(id: Int): Log {
        return repository.getLogById(id)
    }
}