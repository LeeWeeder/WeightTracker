package com.leeweeder.weighttracker.ui.home

import com.leeweeder.weighttracker.domain.model.Log
import com.leeweeder.weighttracker.util.Weight

data class HomeUiState(
    val fiveMostRecentLogs: List<Log> = emptyList(),
    val goalWeight: Int = 0,
    val oldestLogWeight: Weight? = null
) {
    val mostRecentLog: Log?
        get() = fiveMostRecentLogs.firstOrNull()

    private val previousMostRecentLog: Log?
        get() = if (fiveMostRecentLogs.size > 1) fiveMostRecentLogs[1] else null

    val differenceFromPrevious: Float?
        get() = previousMostRecentLog?.weight?.value?.let {
            mostRecentLog?.weight?.value
                ?.minus(it)
        }

    val differenceFromGoal: Float?
        get() = mostRecentLog?.weight?.value?.let {
            goalWeight.minus(it)
        }
}
