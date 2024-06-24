package com.leeweeder.weighttracker.util

import com.leeweeder.weighttracker.ui.util.formatToOneDecimalPlace

data class Weight(
    val value: Float
) {
    val displayValue: String
        get() = value.formatToOneDecimalPlace()
}

const val MAX_WEIGHT = 450f