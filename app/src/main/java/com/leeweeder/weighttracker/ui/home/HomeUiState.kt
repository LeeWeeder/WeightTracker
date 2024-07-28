package com.leeweeder.weighttracker.ui.home

import com.leeweeder.weighttracker.domain.model.Log
import com.leeweeder.weighttracker.util.daysOfWeek
import java.time.LocalDate

data class HomeUiState(
    val logsForThisWeek: List<Log> = emptyList(),
    val goalWeight: Int = 0,
    val latestLogPair: LatestLogPair = LatestLogPair()
) {
    val currentWeightDifferenceFromGoal: Float?
        get() = this.latestLogPair.currentLog?.weight?.value?.let {
            goalWeight.minus(it)
        }

    val today: LocalDate = LocalDate.now()

    val daysOfWeek = today.daysOfWeek
}

data class LatestLogPair(val currentLog: Log? = null, val previousLog: Log? = null) {
    val difference: Float?
        get() = previousLog?.weight?.value?.let {
            currentLog?.weight?.value
                ?.minus(it)
        }
}
