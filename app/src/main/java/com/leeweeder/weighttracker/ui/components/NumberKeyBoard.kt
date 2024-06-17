package com.leeweeder.weighttracker.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.leeweeder.weighttracker.R

inline fun <reified T : Number> requireConvertibleTo(valueString: String) {
    if (valueString.toTypeOrNull<T>() == null) {
        throw IllegalArgumentException("Value must be convertible to ${T::class.simpleName}")
    }
}

inline fun <reified T : Number> String.toTypeOrNull(): T? {
    return when (T::class) {
        Double::class -> toDoubleOrNull() as T?
        Int::class -> toIntOrNull() as T?
        else -> null
    }
}

@Composable
fun rememberNumberKeyBoardState(
    defaultValue: String
): NumberKeyBoardState {
    requireConvertibleTo<Double>(defaultValue)
    return remember {
        NumberKeyBoardState(defaultValue)
    }
}

class NumberKeyBoardState(
    defaultValue: String = "0"
) {
    init {
        requireNotNull(defaultValue.toDoubleOrNull()) {
            "Default value must be a number"
        }
    }

    var value by mutableStateOf(defaultValue.let { if (it.toDouble() == 0.0) "0" else it })

    fun clear() {
        value = "0"
    }

    fun appendDigit(digit: Char) {
        val potentialValue = if (value == "0" && digit != '.') digit.toString() else value + digit

        val parsedValue = potentialValue.also { if (it.endsWith('.')) it + '0' }.toDoubleOrNull()

        if (parsedValue != null) {
            if (!potentialValue.contains('.') || potentialValue.split('.')[1].length <= 2) {
                value = potentialValue
            }
        }
    }

    fun deleteLastDigit() {
        value = if (value.length > 1) {
            value.dropLast(1)
        } else {
            "0"
        }
    }
}

@Composable
fun NumberKeyBoard(
    modifier: Modifier = Modifier,
    state: NumberKeyBoardState,
    onValueChange: (String) -> Unit = {}
) {
    LaunchedEffect(key1 = state.value) {
        onValueChange(state.value)
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.height(IntrinsicSize.Min)
    ) {
        Column(modifier = Modifier.weight(3f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            for (i in 0..2) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.height(IntrinsicSize.Min)
                ) {
                    for (j in 0..2) {
                        val digit = (i * 3 + j + 1).digitToChar()
                        DigitKeyButton(
                            onClick = { state.appendDigit(digit) },
                            digit = digit,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DigitKeyButton(
                    onClick = { state.appendDigit('.') },
                    digit = '.',
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    enabled = !state.value.contains('.')
                )
                DigitKeyButton(
                    onClick = { state.appendDigit('0') },
                    digit = '0',
                    modifier = Modifier.weight(1f)
                )
                IconKeyButton(
                    onClick = { state.deleteLastDigit() },
                    iconId = R.drawable.backspace,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun DigitKeyButton(
    onClick: () -> Unit,
    digit: Char,
    modifier: Modifier = Modifier,
    colors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        contentColor = MaterialTheme.colorScheme.onSurface
    ),
    enabled: Boolean = true
) {
    KeyButton(
        onClick = onClick,
        modifier = modifier,
        colors = colors,
        enabled = enabled
    ) {
        Text(
            text = digit.toString(),
            style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Medium)
        )
    }
}

@Composable
private fun IconKeyButton(
    onClick: () -> Unit,
    @DrawableRes iconId: Int,
    modifier: Modifier = Modifier
) {
    KeyButton(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.tertiary,
            contentColor = MaterialTheme.colorScheme.onTertiary
        )
    ) {
        Icon(painterResource(id = iconId), contentDescription = null, Modifier.size(36.dp))
    }
}

@Composable
private fun KeyButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    enabled: Boolean = true,
    content: @Composable () -> Unit
) {
    Button(
        onClick = onClick,
        colors = colors,
        modifier = modifier.fillMaxSize(),
        enabled = enabled
    ) {
        content()
    }
}