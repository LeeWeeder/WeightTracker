package com.leeweeder.weighttracker.domain.usecases.log

import com.leeweeder.weighttracker.domain.repository.LogRepository
import com.leeweeder.weighttracker.ui.util.toEpochMilli
import java.time.LocalDate

class IsOlderThanAll(
    private val repository: LogRepository
) {
    suspend operator fun invoke(date: LocalDate): Boolean {
        return repository.isOlderThanAll(date.toEpochMilli())
    }
}