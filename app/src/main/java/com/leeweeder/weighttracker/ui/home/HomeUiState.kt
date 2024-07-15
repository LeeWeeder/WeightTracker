package com.leeweeder.weighttracker.ui.home

import com.leeweeder.weighttracker.domain.model.Log
import com.leeweeder.weighttracker.util.daysOfWeek
import java.time.LocalDate

data class HomeUiState(
    val logsForThisWeek: List<Log> = emptyList(),
    val goalWeight: Int = 0
) {
    val mostRecentLog: Log?
        get() = logsForThisWeek.lastOrNull()

    private val previousMostRecentLog: Log?
        get() {
            val logsForThisWeekSize = logsForThisWeek.size
            return if (logsForThisWeekSize > 1) logsForThisWeek[logsForThisWeekSize - 2] else null
        }

    val mostRecentDifferenceFromPrevious: Float?
        get() = previousMostRecentLog?.weight?.value?.let {
            mostRecentLog?.weight?.value
                ?.minus(it)
        }

    val mostRecentDifferenceFromGoal: Float?
        get() = mostRecentLog?.weight?.value?.let {
            goalWeight.minus(it)
        }

    val today: LocalDate = LocalDate.now()

    val daysOfWeek = today.daysOfWeek
}
