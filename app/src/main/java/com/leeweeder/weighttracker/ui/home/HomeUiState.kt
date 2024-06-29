package com.leeweeder.weighttracker.ui.home

import com.leeweeder.weighttracker.domain.model.Log
import com.leeweeder.weighttracker.util.StartingWeightModel
import com.leeweeder.weighttracker.util.Weight
import kotlin.math.absoluteValue

data class HomeUiState(
    val fiveMostRecentLogs: List<Log> = emptyList(),
    val goalWeight: Int = 0,
    val oldestLogWeight: Weight? = null,
    val startingWeight: StartingWeightModel? = null
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

    val goalProgress: Float?
        get() {
            return if (startingWeight != null && mostRecentLog != null && startingWeight.weight != null) {
                val startingWeightDifferenceFromCurrentWeight =
                    (startingWeight.weight.minus(mostRecentLog!!.weight.value)).absoluteValue
                val goalWeightDifferenceFromStartingWeight =
                    (goalWeight - startingWeight.weight).absoluteValue
                val value = startingWeightDifferenceFromCurrentWeight.div(
                    goalWeightDifferenceFromStartingWeight
                )
                if (value >= 1f) 1f else value
            } else {
                null
            }
        }
}
