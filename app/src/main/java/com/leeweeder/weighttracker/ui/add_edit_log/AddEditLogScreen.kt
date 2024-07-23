package com.leeweeder.weighttracker.ui.add_edit_log

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.leeweeder.numberslider.NumberSlider
import com.leeweeder.weighttracker.R
import com.leeweeder.weighttracker.ui.LocalNavController
import com.leeweeder.weighttracker.ui.components.AlertDialog
import com.leeweeder.weighttracker.ui.components.InvalidValueAlertDialog
import com.leeweeder.weighttracker.ui.util.format
import com.leeweeder.weighttracker.util.Weight
import com.leeweeder.weighttracker.util.toEpochMilli
import com.leeweeder.weighttracker.util.toWeight
import java.time.LocalDate

@Composable
fun AddEditLogScreen(
    viewModel: AddEditLogViewModel = hiltViewModel()
) {
    val addEditLogUiState = viewModel.addEditLogUiState.value
    AddEditLogScreen(
        uiState = addEditLogUiState,
        onEvent = viewModel::onEvent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AddEditLogScreen(
    uiState: AddEditLogUiState,
    onEvent: (AddEditLogEvent) -> Unit,
) {
    val textFieldValue = remember(uiState.weight) {
        mutableStateOf(uiState.weight.toString())
    }

    val navController = LocalNavController.current
    if (uiState.datePickerDialogVisible) {
        val datePickerState =
            rememberDatePickerState(
                initialSelectedDateMillis = uiState.date.toEpochMilli(),
                selectableDates = object : SelectableDates {
                    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                        return utcTimeMillis <= System.currentTimeMillis()
                    }
                })

        val onDismissRequest =
            { onEvent(AddEditLogEvent.DatePickerDialogToggleVisibility(show = false)) }

        DatePickerDialog(
            onDismissRequest = onDismissRequest,
            confirmButton = {
                TextButton(
                    onClick = {
                        onEvent(AddEditLogEvent.SetDate(datePickerState.selectedDateMillis!!))
                    }
                ) {
                    Text("Okay")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onDismissRequest
                ) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
    val isInvalidValueAlertDialogVisible = remember {
        mutableStateOf(false)
    }

    InvalidValueAlertDialog(
        visible = isInvalidValueAlertDialogVisible.value,
        onDismissRequest = { isInvalidValueAlertDialogVisible.value = false },
        title = "Weight value can't be zero",
        text = "Please enter a value greater than zero."
    )

    fun saveLog() {
        val textFieldWeightValue = textFieldValue.value.toFloatOrNull()
        val newWeight =
            if (textFieldWeightValue == null || uiState.weight == textFieldWeightValue.toWeight() || textFieldWeightValue == 0f) {
                uiState.weight
            } else {
                textFieldWeightValue.toWeight()
            }

        if (newWeight == Weight(0f)) {
            isInvalidValueAlertDialogVisible.value = true
            return
        }

        if (newWeight != uiState.weight) {
            onEvent(AddEditLogEvent.SetWeight(newWeight.value))
        }

        onEvent(AddEditLogEvent.SaveLog)
        navController.navigateUp()
    }

    val isConfirmUpdateDialogVisible = remember {
        mutableStateOf(false)
    }

    AlertDialog(
        visible = isConfirmUpdateDialogVisible.value,
        onDismissRequest = { isConfirmUpdateDialogVisible.value = false },
        title = "Continue update?",
        text = "Are you sure to override the weight for this date (${uiState.date.format("MM/d/yyyy")})? This cannot be undone.",
        confirmButton = {
            TextButton(onClick = {
                saveLog()
                isConfirmUpdateDialogVisible.value = false
            }) {
                Text(text = "Update", color = MaterialTheme.colorScheme.tertiary)
            }
        },
        dismissButton = {
            TextButton(onClick = { isConfirmUpdateDialogVisible.value = false }) {
                Text(text = "Cancel")
            }
        }
    )

    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = "Add weight",
                        style = MaterialTheme.typography.displaySmall
                    )
                },
                actions = {
                    TextButton(onClick = {
                        if (uiState.currentLogId == DEFAULT_LOG_ID) saveLog() else isConfirmUpdateDialogVisible.value =
                            true
                    }) {
                        Text(text = "Save")
                    }
                    if (uiState.currentLogId != DEFAULT_LOG_ID) {
                        Box {
                            var menuExpanded by remember {
                                mutableStateOf(false)
                            }
                            IconButton(onClick = {
                                menuExpanded = true
                            }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.more_vert),
                                    contentDescription = "More menu"
                                )
                            }
                            DropdownMenu(expanded = menuExpanded, onDismissRequest = {
                                menuExpanded = false
                            }) {
                                DropdownMenuItem(text = {
                                    Text(text = "Delete")
                                }, onClick = {
                                    onEvent(AddEditLogEvent.DeleteLog)
                                    menuExpanded = false
                                    navController.popBackStack()
                                })
                            }
                        }
                    }
                }, navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.close),
                            contentDescription = "Close add edit screen"
                        )
                    }
                })
        },
        modifier = Modifier.clickable(interactionSource = remember {
            MutableInteractionSource()
        }, indication = null, onClick = {
            focusManager.clearFocus()
        })
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            val labelStyle = MaterialTheme.typography.labelLarge
            ListItem(
                headlineContent = {
                    Text(text = "Date", style = labelStyle)
                },
                trailingContent = {
                    val today = LocalDate.now()
                    val defaultPattern = "EEE, MMM d"
                    val pattern =
                        if (today.year == uiState.date.year) defaultPattern else "$defaultPattern, yyyy"
                    Text(
                        text = uiState.date.format(pattern, true),
                        modifier = Modifier
                            .clip(MaterialTheme.shapes.small)
                            .clickable {
                                onEvent(
                                    AddEditLogEvent.DatePickerDialogToggleVisibility(
                                        show = true
                                    )
                                )
                            }
                            .padding(10.dp),
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            )
            ListItem(headlineContent = { Text(text = "Weight", style = labelStyle) })

            NumberSlider(
                value = uiState.weight.value,
                onValueChange = {
                    onEvent(AddEditLogEvent.SetWeight(it))
                },
                modifier = Modifier.padding(top = 64.dp),
                maxValue = 500,
                minValue = 0,
                onTextFieldValueChange = {
                    textFieldValue.value = it
                })
        }
    }
}