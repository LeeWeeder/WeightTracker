package com.leeweeder.weighttracker.ui.util

import java.util.Locale

fun Float.formatToOneDecimalPlace(showTrailingZero: Boolean = true, showPlusSign: Boolean = false): String {
    val formatted = String.format(locale = Locale.getDefault(), format = "%.1f", this)
    if (!showTrailingZero) {
        return formatted.trimEnd('0').trimEnd('.')
    }

    if (showPlusSign) {
        return if (this > 0) {
            "+$formatted"
        } else {
            formatted
        }
    }

    return formatted
}