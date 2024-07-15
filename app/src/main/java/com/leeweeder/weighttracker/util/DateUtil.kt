package com.leeweeder.weighttracker.util

import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.TemporalAdjusters

val LocalDate.daysOfWeek: List<LocalDate>
    get() {
        val daysOfWeek = mutableListOf<LocalDate>()
        var currentDay = this.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))

        repeat(7) {
            daysOfWeek.add(currentDay)
            currentDay = currentDay.plusDays(1)
        }

        return daysOfWeek
    }

fun LocalDate.toEpochMilli(): Long {
    val startOfDay = LocalTime.MIDNIGHT
    val zonedDateTime = ZonedDateTime.of(this, startOfDay, ZoneId.of("UTC"))
    return zonedDateTime.toInstant().toEpochMilli()
}

fun epochMillisToLocalDate(millis: Long): LocalDate {
    return Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
}