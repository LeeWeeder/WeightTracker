package com.leeweeder.weighttracker.domain.usecases.log

import com.leeweeder.weighttracker.domain.model.Log
import com.leeweeder.weighttracker.domain.repository.LogRepository

class UpdateLog(
    private val repository: LogRepository
) {
    suspend operator fun invoke(log: Log) {
        repository.updateLog(log)
    }
}