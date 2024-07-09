package com.leeweeder.weighttracker.data.datasource

import androidx.room.TypeConverter
import com.leeweeder.weighttracker.util.Weight
import java.time.LocalDate

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
    fun fromLocalDate(date: LocalDate): Long {
        return date.toEpochDay()
    }

    @TypeConverter
    fun toLocalDate(value: Long): LocalDate {
        return LocalDate.ofEpochDay(value)
    }
}