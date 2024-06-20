package com.leeweeder.weighttracker.data.datasource

import androidx.room.TypeConverter
import com.leeweeder.weighttracker.util.Weight

class Converters {
    @TypeConverter
    fun fromWeight(weight: Weight): Double {
        return weight.value
    }

    @TypeConverter
    fun toWeight(value: Double): Weight {
        return Weight(value)
    }
}