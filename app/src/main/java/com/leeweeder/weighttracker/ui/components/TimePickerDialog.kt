package com.leeweeder.weighttracker.ui.components

import android.icu.util.Calendar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.leeweeder.weighttracker.R
import java.time.Instant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    state: TimePickerState,
    onCancel: () -> Unit,
    onConfirm: (Long) -> Unit,
    visible: Boolean = false
) {
    val showingPicker = remember { mutableStateOf(true) }
    val title = if (showingPicker.value) {
        "Select Time "
    } else {
        "Enter Time"
    }
    if (visible) {
        Dialog(
            onDismissRequest = onCancel,
            properties = DialogProperties(
                usePlatformDefaultWidth = false
            ),
        ) {
            Surface(
                shape = MaterialTheme.shapes.extraLarge,
                tonalElevation = 6.dp,
                modifier = Modifier
                    .width(IntrinsicSize.Min)
                    .height(IntrinsicSize.Min)
                    .background(
                        shape = MaterialTheme.shapes.extraLarge,
                        color = MaterialTheme.colorScheme.surface
                    ),
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 20.dp),
                        text = title,
                        style = MaterialTheme.typography.labelMedium
                    )
                    if (showingPicker.value) {
                        TimePicker(state = state)
                    } else {
                        TimeInput(state = state)
                    }
                    Row(
                        modifier = Modifier
                            .height(40.dp)
                            .fillMaxWidth()
                    ) {
                        IconButton(onClick = { showingPicker.value = !showingPicker.value }) {
                            val iconId = if (showingPicker.value) {
                                R.drawable.keyboard
                            } else {
                                R.drawable.schedule
                            }
                            Icon(
                                painterResource(id = iconId),
                                contentDescription = if (showingPicker.value) {
                                    "Switch to Text Input"
                                } else {
                                    "Switch to Touch Input"
                                }
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        TextButton(
                            onClick = onCancel
                        ) { Text("Cancel") }
                        TextButton(
                            onClick = {
                                val cal = Calendar.getInstance()
                                cal.set(Calendar.HOUR_OF_DAY, state.hour)
                                cal.set(Calendar.MINUTE, state.minute)
                                cal.isLenient = false
                                onConfirm(cal.timeInMillis)
                                onCancel()
                            }
                        ) { Text("Okay") }
                    }
                }
            }
        }
    }
}