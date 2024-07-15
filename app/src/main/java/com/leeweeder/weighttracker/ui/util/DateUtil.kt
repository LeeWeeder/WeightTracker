package com.leeweeder.weighttracker.ui.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter

val LocalDate.relativeDate: String?
    get() {
        val today = LocalDate.now()
        val yesterday = today.minusDays(1)
        return when (this) {
            today -> RelativeDates.Today.name
            yesterday -> RelativeDates.Yesterday.name
            else -> null
        }
    }

enum class RelativeDates {
    Today,
    Yesterday
}

fun LocalDate.format(pattern: String, useRelativeDates: Boolean = false): String {
    val selectedDate = this
    val formatted = selectedDate.format(DateTimeFormatter.ofPattern(pattern))

    if (useRelativeDates) {
        return selectedDate.relativeDate ?: formatted
    }

    return formatted
}