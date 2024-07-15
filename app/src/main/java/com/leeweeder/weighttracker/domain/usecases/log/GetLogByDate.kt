package com.leeweeder.weighttracker.domain.usecases.log

import com.leeweeder.weighttracker.domain.model.Log
import com.leeweeder.weighttracker.domain.repository.LogRepository
import java.time.LocalDate

class GetLogByDate(
    private val repository: LogRepository
) {
    suspend operator fun invoke(date: LocalDate): Log? {
        return repository.getLogByDate(date.toEpochDay())
    }
}