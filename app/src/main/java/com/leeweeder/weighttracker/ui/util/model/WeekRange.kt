package com.leeweeder.weighttracker.ui.util.model

import com.leeweeder.weighttracker.ui.util.format
import java.time.LocalDate
import java.time.temporal.ChronoUnit

private const val DAY_GAP = 6L

data class WeekRange(val start: LocalDate, val end: LocalDate) {
    init {
        require(ChronoUnit.DAYS.between(start, end) == DAY_GAP) {
            "Date range must have $DAY_GAP days gap in between"
        }
    }

    fun format(pattern: String): String {
        val isSameMonth = start.month == end.month
        val isSameYear = start.year == end.year

        val endFormatPattern =
            if (isSameMonth) sanitizeFormatPattern(pattern, DateUnit.Month) else pattern
        val startFormatPattern =
            if (isSameYear) sanitizeFormatPattern(pattern, DateUnit.Year) else pattern

        return "${start.format(startFormatPattern)} – ${end.format(endFormatPattern)}"
    }

    private fun sanitizeFormatPattern(pattern: String, dateUnit: DateUnit): String {
        return when (dateUnit) {
            DateUnit.Month -> {
                pattern.dropWhile { it == 'M' || it == 'L' }
            }

            DateUnit.Year -> {
                pattern.dropWhile { it == 'y' }
            }
        }
    }
}

private enum class DateUnit {
    Month,
    Year
}