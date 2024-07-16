package com.leeweeder.weighttracker.util

import com.leeweeder.weighttracker.ui.util.formatToOneDecimalPlace

data class Weight(
    val value: Float
) {
    val displayValue: String
        get() = value.formatToOneDecimalPlace()
}

fun Float.toWeight(): Weight = Weight(this)