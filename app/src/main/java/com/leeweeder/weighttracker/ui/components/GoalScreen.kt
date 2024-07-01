package com.leeweeder.weighttracker.ui.components

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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.leeweeder.weighttracker.R

@Composable
fun GoalScreen(
    onWeightGoalSet: (weight: Int) -> Unit
) {
    val isValidationDialogVisible = remember {
        mutableStateOf(false)
    }
    val goalWeightState = remember {
        mutableStateOf("60")
    }
    if (isValidationDialogVisible.value) {
        AlertDialog(
            onDismissRequest = { isValidationDialogVisible.value = false },
            confirmButton = {
                TextButton(onClick = { isValidationDialogVisible.value = false }) {
                    Text(text = "Okay")
                }
            },
            icon = {
                Icon(painter = painterResource(id = R.drawable.error), contentDescription = null)
            },
            title = {
                Text(text = "Can't set goal")
            },
            text = {
                Text(text = if (goalWeightState.value.isEmpty()) "Enter a value to set your goal." else "Goal weight must be greater than 0.")
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(horizontal = 16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f)
        ) {
            Spacer(modifier = Modifier.height(150.dp))
            GoalWeightTextField(value = goalWeightState.value) {
                if (it.isEmpty()) {
                    goalWeightState.value = it
                    return@GoalWeightTextField
                }
                val value = it.toIntOrNull() ?: return@GoalWeightTextField
                if (value < 0) return@GoalWeightTextField
                if (it.length > 3) return@GoalWeightTextField
                goalWeightState.value = value.toString()
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
                        if (goalWeightState.value.isEmpty()) {
                            goalWeightState.value = "0"
                            return@ControlButton
                        }
                        val value = goalWeightState.value.toInt()
                        if (value > 0) {
                            goalWeightState.value = (value - 1).toString()
                        }
                    }
                )
                ControlButton(
                    type = ControlButtonType.Increase,
                    modifier = Modifier.weight(1f),
                    position = ControlButtonPosition.Right,
                    onClick = {
                        if (goalWeightState.value.isEmpty()) {
                            goalWeightState.value = "1"
                            return@ControlButton
                        }
                        val value = goalWeightState.value.toInt()
                        val potentialValue = (value + 1).toString()
                        if (potentialValue.length <= 3) {
                            goalWeightState.value = potentialValue
                        }
                    }
                )
            }
        }
        Button(
            onClick = {
                val goalWeight = goalWeightState.value
                if (goalWeight.isEmpty() || goalWeight.toInt() == 0) {
                    isValidationDialogVisible.value = true
                    return@Button
                }

                onWeightGoalSet(goalWeight.toInt())
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
private fun GoalWeightTextField(value: String, onValueChange: (String) -> Unit) {
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
        cursorBrush = if (value.isEmpty()) SolidColor(Color.Unspecified) else SolidColor(
            MaterialTheme.colorScheme.primary
        ),
        decorationBox = { innerTextField ->
            Box(contentAlignment = Alignment.Center) {
                if (value.isEmpty()) {
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
        }
    )
}