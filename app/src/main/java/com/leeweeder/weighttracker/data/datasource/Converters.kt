package com.leeweeder.weighttracker.data.datasource

import androidx.room.TypeConverter
import com.leeweeder.weighttracker.util.Weight
import java.time.Instant

class Converters {
    @TypeConverter
    fun fromTimestamp(date: Instant): Long {
        return date.toEpochMilli()
    }

    @TypeConverter
    fun toTimestamp(value: Long): Instant {
        return Instant.ofEpochMilli(value)
    }

    @TypeConverter
    fun fromWeight(weight: Weight): Double {
        return weight.value
    }

    @TypeConverter
    fun toWeight(value: Double): Weight {
        return Weight(value)
    }
}