package com.leeweeder.weighttracker.util

import com.leeweeder.weighttracker.ui.util.formatToOneDecimalPlace

data class Weight(
    val value: Float
) {
    val displayValue: String
        get() = value.formatToOneDecimalPlace()

    override fun equals(other: Any?): Boolean {
        return other is Weight && this.value == other.value
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }
}

fun Number.toWeight(): Weight = Weight(this.toFloat())