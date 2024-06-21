package com.leeweeder.weighttracker.ui.add_edit_log

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.leeweeder.weighttracker.R
import com.leeweeder.weighttracker.ui.AddEditLogSharedViewModel
import com.leeweeder.weighttracker.ui.LocalNavController
import com.leeweeder.weighttracker.ui.components.NumberKeyBoard
import com.leeweeder.weighttracker.ui.components.rememberNumberKeyBoardState
import com.leeweeder.weighttracker.ui.util.format
import com.leeweeder.weighttracker.ui.util.formatToTwoDecimalPlaces
import com.leeweeder.weighttracker.ui.util.toEpochMilli
import com.leeweeder.weighttracker.util.MAX_WEIGHT

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

    val numberKeyBoardState = rememberNumberKeyBoardState(
        defaultValue = uiState.weight.value.formatToTwoDecimalPlaces(false),
        maxValue = MAX_WEIGHT
    )
    Box {
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
                        Button(onClick = {
                            onEvent(AddEditLogEvent.SaveLog)
                            navController.navigateUp()
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
                        Text(text = "Time", style = labelStyle)
                    },
                    trailingContent = {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = uiState.date.format("EEE, mm d", true),
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
                    },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                )
                ElevatedCard(
                    modifier = Modifier.fillMaxSize(),
                    shape = MaterialTheme.shapes.extraLarge.copy(
                        bottomStart = CornerSize(0.dp),
                        bottomEnd = CornerSize(0.dp)
                    ),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Weight",
                            style = labelStyle,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(
                                    alpha = 0.2f
                                )
                            )
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                AnimatedVisibility(visible = uiState.weight.value != 0.0) {
                                    IconButton(onClick = {
                                        numberKeyBoardState.clear()
                                    }) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.cancel),
                                            contentDescription = "Clear input",
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }

                                Column(
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .fillMaxWidth(),
                                    horizontalAlignment = Alignment.End
                                ) {
                                    Text(
                                        text = numberKeyBoardState.value,
                                        style = MaterialTheme.typography.displayLarge.copy(fontSize = (MaterialTheme.typography.displayLarge.fontSize.value + 48).sp),
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Text(text = "kg", style = MaterialTheme.typography.titleLarge)
                                }
                            }
                        }
                    }
                }
            }
        }

        NumberKeyBoard(
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.BottomCenter),
            state = numberKeyBoardState
        ) {
            onEvent(AddEditLogEvent.SetWeight(it.toDouble()))
        }
    }
}