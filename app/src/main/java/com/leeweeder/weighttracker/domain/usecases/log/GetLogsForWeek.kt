package com.leeweeder.weighttracker.domain.usecases.log

import com.leeweeder.weighttracker.domain.model.Log
import com.leeweeder.weighttracker.domain.repository.LogRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transformLatest
import java.time.DayOfWeek
import java.time.LocalDate

private const val NUMBER_OF_DAYS_IN_WEEK = 7
private const val PADDING = NUMBER_OF_DAYS_IN_WEEK - 1

class GetLogsForWeek(
    private val repository: LogRepository
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(date: LocalDate): Flow<List<Log>> {
        val logs = repository.getLogsAroundDate(date.toEpochDay(), PADDING)
        val daysOfWeek = getDaysOfWeek(date)
        return logs.transformLatest {
            emit(it.filter { log ->
                daysOfWeek.contains(log.date)
            })
        }
    }

    fun getDaysOfWeek(date: LocalDate): List<LocalDate> {
        val daysOfWeek = mutableListOf<LocalDate>()
        var currentDay = date.with(DayOfWeek.SUNDAY).minusWeeks(1)

        repeat(7) {
            daysOfWeek.add(currentDay)
            currentDay = currentDay.plusDays(1)
        }

        return daysOfWeek
    }
}