package com.leeweeder.weighttracker.ui.home

import com.leeweeder.weighttracker.domain.model.Log

data class HomeUiState(
    val fiveMostRecentLogs: List<Log> = emptyList(),
    val goalWeight: Int = 0
) {
    val mostRecentLog: Log?
        get() = fiveMostRecentLogs.firstOrNull()

    private val previousMostRecentLog: Log?
        get() = if (fiveMostRecentLogs.size > 1) fiveMostRecentLogs[1] else null

    val mostRecentDifferenceFromPrevious: Float?
        get() = previousMostRecentLog?.weight?.value?.let {
            mostRecentLog?.weight?.value
                ?.minus(it)
        }

    val mostRecentDifferenceFromGoal: Float?
        get() = mostRecentLog?.weight?.value?.let {
            goalWeight.minus(it)
        }
}
