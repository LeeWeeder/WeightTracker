package com.leeweeder.weighttracker.domain.usecases.log

import com.leeweeder.weighttracker.domain.model.Log
import com.leeweeder.weighttracker.domain.repository.LogRepository
import com.leeweeder.weighttracker.util.daysOfWeek
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transformLatest
import java.time.LocalDate

private const val NUMBER_OF_DAYS_IN_WEEK = 7
private const val PADDING = NUMBER_OF_DAYS_IN_WEEK - 1

class GetLogsForThisWeek(
    private val repository: LogRepository
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(date: LocalDate): Flow<List<Log>> {
        val logs = repository.getLogsAroundDate(date.toEpochDay(), PADDING)
        val daysOfWeek = date.daysOfWeek
        return logs.transformLatest {
            emit(it.filter { log ->
                daysOfWeek.contains(log.date)
            })
        }
    }
}