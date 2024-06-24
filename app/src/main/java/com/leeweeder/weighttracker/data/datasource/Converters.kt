package com.leeweeder.weighttracker.data.datasource

import androidx.room.TypeConverter
import com.leeweeder.weighttracker.util.Weight
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class Converters {
    @TypeConverter
    fun fromWeight(weight: Weight): Float {
        return weight.value
    }

    @TypeConverter
    fun toWeight(value: Float): Weight {
        return Weight(value)
    }

    @TypeConverter
    fun fromLocalDate(date: LocalDate): String {
        return date.format(DateTimeFormatter.ISO_LOCAL_DATE)
    }

    @TypeConverter
    fun toLocalDate(value: String): LocalDate {
        return LocalDate.parse(value)
    }
}