package com.leeweeder.weighttracker.ui.util

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.ime
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
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

@Composable
fun isKeyboardClosing(): Boolean {
    val isKeyBoardClosing = remember {
        mutableStateOf(false)
    }
    val imeInsets = WindowInsets.ime.asPaddingValues()
    val previousBottomPadding = remember {
        mutableStateOf(0.dp)
    }
    LaunchedEffect(key1 = imeInsets.calculateBottomPadding()) {
        isKeyBoardClosing.value = imeInsets.calculateBottomPadding() < previousBottomPadding.value
        previousBottomPadding.value = imeInsets.calculateBottomPadding()
    }
    return isKeyBoardClosing.value
}