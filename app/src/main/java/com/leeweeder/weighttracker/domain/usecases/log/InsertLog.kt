package com.leeweeder.weighttracker.domain.usecases.log

import com.leeweeder.weighttracker.domain.model.Log
import com.leeweeder.weighttracker.domain.repository.LogRepository

class InsertLog(
    private val repository: LogRepository
) {
    suspend operator fun invoke(log: Log): Long {
        return repository.insertLog(log)
    }
}