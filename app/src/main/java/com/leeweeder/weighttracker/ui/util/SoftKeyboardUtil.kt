package com.leeweeder.weighttracker.ui.util

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.ime
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp

object SoftKeyboardController {
    @Composable
    fun isClosing(): Boolean {
        val isClosing = remember {
            mutableStateOf(false)
        }
        val imeInsets = WindowInsets.ime.asPaddingValues()
        val previousBottomPadding = remember {
            mutableStateOf(0.dp)
        }
        LaunchedEffect(key1 = imeInsets.calculateBottomPadding()) {
            isClosing.value =
                imeInsets.calculateBottomPadding() < previousBottomPadding.value
            previousBottomPadding.value = imeInsets.calculateBottomPadding()
        }
        return isClosing.value
    }
}