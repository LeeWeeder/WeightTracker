package com.leeweeder.weighttracker.util

import com.leeweeder.weighttracker.ui.util.formatToTwoDecimalPlaces

data class Weight(
    val value: Double
) {
    val displayValue: String
        get() = value.formatToTwoDecimalPlaces()
}

const val MAX_WEIGHT = 450.0