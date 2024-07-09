package com.leeweeder.weighttracker.ui.home.util

import com.leeweeder.weighttracker.ui.home.daysOfTheWeek
import com.leeweeder.weighttracker.ui.home.goalWeightKey
import com.patrykandpatrick.vico.core.cartesian.data.AxisValueOverrider
import com.patrykandpatrick.vico.core.common.data.ExtraStore
import kotlin.math.roundToInt

fun AxisValueOverrider.Companion.weightTrackerValueOverrider(): AxisValueOverrider {
    return object : AxisValueOverrider {
        override fun getMinY(minY: Float, maxY: Float, extraStore: ExtraStore): Float {
            return (minY - 1).roundToInt().toFloat()
        }

        override fun getMaxY(minY: Float, maxY: Float, extraStore: ExtraStore): Float {
            val newMaxY = if (maxY > extraStore[goalWeightKey]) maxY else extraStore[goalWeightKey]
            val rangeTenPercent = (newMaxY - minY) * 0.1f
            val threshold = if (newMaxY == minY || (rangeTenPercent).roundToInt() == 0) 1f else rangeTenPercent
            return (newMaxY + threshold.roundToInt().toFloat())
        }

        override fun getMinX(minX: Float, maxX: Float, extraStore: ExtraStore): Float {
            return extraStore[daysOfTheWeek].minOf { it.toEpochDay().toFloat() }
        }

        override fun getMaxX(minX: Float, maxX: Float, extraStore: ExtraStore): Float {
            return extraStore[daysOfTheWeek].maxOf {  it.toEpochDay().toFloat() }
        }
    }
}