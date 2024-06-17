package com.leeweeder.weighttracker.ui.util

import android.icu.util.Calendar
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

fun Instant.getFormattedDate(
    pattern: String = "EEE, MMM dd, yyyy, h:mm a",
    useRelativeDates: Boolean = false
): String {
    val selectedDate = this
    val dateToday = Instant.now()

    val difference = ChronoUnit.DAYS.between(selectedDate, dateToday)
    val date = LocalDateTime.ofInstant(this, ZoneId.systemDefault())
    val format = DateTimeFormatter.ofPattern(pattern)
    if (useRelativeDates) {
        return when (difference) {
            0L -> "Today"
            1L -> "Yesterday"
            else -> date.format(format)
        }
    }

    return date.format(format)
}

fun Instant.getDatePickerCompatibleFormat(): Instant {
    val currentDate = this
    val calendar = Calendar.getInstance()
    calendar.set(
        currentDate.getFormattedDate("yyyy").toInt(),
        currentDate.getFormattedDate("MM").toInt() - 1,
        currentDate.getFormattedDate("d").toInt(),
        8,
        0
    )
    return Instant.ofEpochMilli(calendar.timeInMillis)
}

fun Double.formatToTwoDecimalPlaces(showTrailingZero: Boolean = true): String {
    val formatted = String.format(locale = Locale.getDefault(), format = "%.2f", this)
    if (!showTrailingZero) {
        return formatted.trimEnd('0').trimEnd('.')
    }

    return formatted
}