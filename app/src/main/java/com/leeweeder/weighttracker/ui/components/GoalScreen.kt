package com.leeweeder.weighttracker.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.leeweeder.weighttracker.R
import com.leeweeder.weighttracker.ui.util.SoftKeyboardController

@Composable
fun GoalScreen(
    initialValue: Int = 60,
    onWeightGoalSet: (weight: Int) -> Unit
) {
    val isValidationDialogVisible = remember {
        mutableStateOf(false)
    }
    val goalWeightState = remember {
        mutableStateOf(TextFieldValue(text = initialValue.toString()))
    }
    val keepWholeSelection = remember {
        mutableStateOf(false)
    }
    if (keepWholeSelection.value) {
        SideEffect {
            keepWholeSelection.value = false
        }
    }
    AlertDialog(
        visible = isValidationDialogVisible.value,
        onDismissRequest = { isValidationDialogVisible.value = false },
        title = "Can't set goal",
        text = if (goalWeightState.value.text.isEmpty())
            "Enter a value to set your goal."
        else
            "Goal weight must be greater than 0."
    )
    val focusManager = LocalFocusManager.current

    val isKeyboardClosing = SoftKeyboardController.isClosing()

    LaunchedEffect(key1 = isKeyboardClosing) {
        if (isKeyboardClosing) {
            focusManager.clearFocus()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(horizontal = 16.dp)
            .clickable(interactionSource = remember {
                MutableInteractionSource()
            }, indication = null, onClick = {
                focusManager.clearFocus()
            })
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f)
        ) {
            Spacer(modifier = Modifier.height(150.dp))
            GoalWeightTextField(
                value = goalWeightState.value,
                modifier = Modifier.onFocusChanged { focusState ->
                    if (focusState.isFocused) {
                        val text = goalWeightState.value.text
                        goalWeightState.value =
                            goalWeightState.value.copy(selection = TextRange(0, text.length))
                        keepWholeSelection.value = true
                    }
                }) { newValue ->
                if (newValue.text.isEmpty()) {
                    goalWeightState.value = newValue
                    return@GoalWeightTextField
                }
                val value = newValue.text.toIntOrNull() ?: return@GoalWeightTextField
                if (value < 0) return@GoalWeightTextField
                if (value > 500) return@GoalWeightTextField
                if (keepWholeSelection.value) {
                    keepWholeSelection.value = false
                    goalWeightState.value = newValue.copy(text = value.toString(), selection = TextRange(0, newValue.text.length))
                } else {
                    goalWeightState.value = newValue
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(text = "KILOGRAMS", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(36.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                ControlButton(
                    type = ControlButtonType.Decrease,
                    modifier = Modifier.weight(1f),
                    position = ControlButtonPosition.Left,
                    onClick = {
                        focusManager.clearFocus()
                        val goalWeight = goalWeightState.value
                        if (goalWeight.text.isEmpty()) {
                            goalWeightState.value = goalWeight.copy(text = "0")
                            return@ControlButton
                        }
                        val value = goalWeight.text.toInt()
                        if (value > 0) {
                            goalWeightState.value = goalWeight.copy(text = (value - 1).toString())
                        }
                    }
                )
                ControlButton(
                    type = ControlButtonType.Increase,
                    modifier = Modifier.weight(1f),
                    position = ControlButtonPosition.Right,
                    onClick = {
                        focusManager.clearFocus()
                        val goalWeight = goalWeightState.value
                        if (goalWeight.text.isEmpty()) {
                            goalWeightState.value = goalWeight.copy(text = "1")
                            return@ControlButton
                        }
                        val value = goalWeight.text.toInt()
                        val potentialValue = (value + 1)
                        if (potentialValue <= 500) {
                            goalWeightState.value =
                                goalWeight.copy(text = potentialValue.toString())
                        }
                    }
                )
            }
        }
        Button(
            onClick = {
                val goalWeight = goalWeightState.value
                if (goalWeight.text.isEmpty() || goalWeight.text.toInt() == 0) {
                    isValidationDialogVisible.value = true
                    return@Button
                }

                onWeightGoalSet(goalWeight.text.toInt())
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            Text(text = "Set goal")
        }
    }
}

enum class ControlButtonType {
    Increase,
    Decrease
}

enum class ControlButtonPosition {
    Left,
    Right
}

@Composable
private fun ControlButton(
    type: ControlButtonType,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    position: ControlButtonPosition
) {
    val largeShapeRadius = 16.dp
    val smallShapeRadius = 2.dp
    val leftShape = RoundedCornerShape(
        topStart = largeShapeRadius,
        topEnd = smallShapeRadius,
        bottomStart = largeShapeRadius,
        bottomEnd = smallShapeRadius
    )
    val rightShape = RoundedCornerShape(
        topStart = smallShapeRadius,
        topEnd = largeShapeRadius,
        bottomStart = smallShapeRadius,
        bottomEnd = largeShapeRadius
    )
    val shape = when (position) {
        ControlButtonPosition.Left -> leftShape
        ControlButtonPosition.Right -> rightShape
    }
    ElevatedButton(
        onClick = onClick, modifier = modifier
            .height(70.dp),
        shape = shape
    ) {
        Icon(
            painter = painterResource(
                id = when (type) {
                    ControlButtonType.Increase -> R.drawable.add
                    ControlButtonType.Decrease -> R.drawable.remove
                }
            ),
            contentDescription = "${type.name} goal weight value by one"
        )
    }
}

@Composable
private fun GoalWeightTextField(
    value: TextFieldValue,
    modifier: Modifier = Modifier,
    onValueChange: (TextFieldValue) -> Unit
) {
    val fontSize = (MaterialTheme.typography.displayLarge.fontSize.value + 36f).sp
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = MaterialTheme.typography.displayLarge.copy(
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Medium,
            fontSize = fontSize,
            textAlign = TextAlign.Center
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        cursorBrush = if (value.text.isEmpty()) SolidColor(Color.Unspecified) else SolidColor(
            MaterialTheme.colorScheme.primary
        ),
        decorationBox = { innerTextField ->
            Box(contentAlignment = Alignment.Center) {
                if (value.text.isEmpty()) {
                    Text(
                        text = "Goal weight",
                        style = MaterialTheme.typography.displayMedium,
                        color = MaterialTheme.colorScheme.outline,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                innerTextField()
            }
        },
        modifier = modifier
    )
}