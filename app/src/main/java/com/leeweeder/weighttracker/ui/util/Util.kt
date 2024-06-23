package com.leeweeder.weighttracker.ui.util

import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

fun Double.formatToOneDecimalPlace(showTrailingZero: Boolean = true): String {
    val formatted = String.format(locale = Locale.getDefault(), format = "%.1f", this)
    if (!showTrailingZero) {
        return formatted.trimEnd('0').trimEnd('.')
    }

    return formatted
}

fun LocalDate.toEpochMilli(): Long {
    val startOfDay = LocalTime.MIDNIGHT
    val zonedDateTime = ZonedDateTime.of(this, startOfDay, ZoneId.of("UTC"))
    return zonedDateTime.toInstant().toEpochMilli()
}

fun epochMillisToLocalDate(millis: Long): LocalDate {
    return Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
}

fun LocalDate.format(pattern: String, useRelativeDates: Boolean = false): String {
    val selectedDate = this
    val formatted = selectedDate.format(DateTimeFormatter.ofPattern(pattern))

    if (useRelativeDates) {
        val today = LocalDate.now()
        val yesterday = today.minusDays(1)
        return when (selectedDate) {
            today -> "Today"
            yesterday -> "Yesterday"
            else -> formatted
        }
    }

    return formatted
}