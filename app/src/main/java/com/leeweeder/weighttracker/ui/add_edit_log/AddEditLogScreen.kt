package com.leeweeder.weighttracker.ui.add_edit_log

import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.leeweeder.numberslider.NumberSlider
import com.leeweeder.weighttracker.R
import com.leeweeder.weighttracker.ui.AddEditLogSharedViewModel
import com.leeweeder.weighttracker.ui.LocalNavController
import com.leeweeder.weighttracker.ui.components.AlertDialog
import com.leeweeder.weighttracker.ui.util.format
import com.leeweeder.weighttracker.util.toEpochMilli
import java.time.LocalDate

@Composable
fun AddEditLogScreen(
    viewModel: AddEditLogViewModel = hiltViewModel(),
    sharedViewModel: AddEditLogSharedViewModel
) {
    val addEditLogUiState = viewModel.addEditLogUiState.value
    val newlyAddedId = viewModel.newlyAddedId.value
    AddEditLogScreen(
        uiState = addEditLogUiState,
        onEvent = viewModel::onEvent,
        newlyAddedId = newlyAddedId,
        onInsertLog = sharedViewModel::addNewLogId
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AddEditLogScreen(
    uiState: AddEditLogUiState,
    newlyAddedId: Long?,
    onEvent: (AddEditLogEvent) -> Unit,
    onInsertLog: (Long) -> Unit
) {
    LaunchedEffect(key1 = newlyAddedId) {
        if (newlyAddedId != null) {
            onInsertLog(newlyAddedId)
        }
    }

    val textFieldValue = remember {
        mutableStateOf("")
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
    val isAlertDialogVisible = remember {
        mutableStateOf(false)
    }

    AlertDialog(
        visible = isAlertDialogVisible.value,
        onDismissRequest = { isAlertDialogVisible.value = false },
        title = "Weight value can't be zero",
        text = "Please enter a value greater than zero."
    )

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
                        onEvent(
                            AddEditLogEvent.SetWeight(
                                value = textFieldValue.value.toFloatOrNull() ?: 0f,
                                onWeightSet = { weight ->
                                    if (weight == 0f) {
                                        isAlertDialogVisible.value = true
                                        return@SetWeight
                                    } else {
                                        onEvent(AddEditLogEvent.SaveLog)
                                        navController.navigateUp()
                                    }
                                })
                        )
                    }) {
                        Text(text = "Save")
                    }
                    if (uiState.currentLogId != -1) {
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
        }
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
                    val pattern = if (today.year == uiState.date.year) defaultPattern else "$defaultPattern, yyyy"
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
                maxValue = 400,
                minValue = 0,
                onTextFieldValueChange = {
                    textFieldValue.value = it
                })
        }
    }
}