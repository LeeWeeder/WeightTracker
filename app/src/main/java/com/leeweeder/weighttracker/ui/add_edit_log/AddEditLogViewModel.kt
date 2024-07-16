package com.leeweeder.weighttracker.ui.add_edit_log

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leeweeder.weighttracker.domain.model.Log
import com.leeweeder.weighttracker.domain.usecases.DataStoreUseCases
import com.leeweeder.weighttracker.domain.usecases.LogUseCases
import com.leeweeder.weighttracker.util.epochMillisToLocalDate
import com.leeweeder.weighttracker.util.toWeight
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

const val LOG_ID_KEY = "logId"

@HiltViewModel
class AddEditLogViewModel @Inject constructor(
    private val logUseCases: LogUseCases,
    private val dataStoreUseCases: DataStoreUseCases,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _addEditLogUiState = mutableStateOf(AddEditLogUiState())
    val addEditLogUiState: State<AddEditLogUiState> = _addEditLogUiState

    private val _newlyAddedId = mutableStateOf<Long?>(null)
    val newlyAddedId: State<Long?> = _newlyAddedId

    init {
        savedStateHandle.get<Int>(LOG_ID_KEY)?.let { logId ->
            if (logId != -1) {
                viewModelScope.launch {
                    logUseCases.getLogById(logId).also { log ->
                        _addEditLogUiState.value = addEditLogUiState.value.copy(
                            currentLogId = log.id,
                            date = log.date,
                            weight = log.weight
                        )
                    }
                }
            } else {
                viewModelScope.launch {
                    logUseCases.getLatestLogs().firstOrNull()?.let { logs ->
                        _addEditLogUiState.value = addEditLogUiState.value.copy(
                            weight = logs.firstOrNull()?.weight
                                ?: dataStoreUseCases.readGoalWeightState().first().toFloat()
                                    .toWeight()
                        )
                    }
                }
            }
        }
    }

    fun onEvent(event: AddEditLogEvent) {
        when (event) {
            is AddEditLogEvent.SetDate -> {
                onEvent(AddEditLogEvent.DatePickerDialogToggleVisibility(show = false))
                _addEditLogUiState.value = addEditLogUiState.value.copy(
                    date = epochMillisToLocalDate(event.millis)
                )
            }

            AddEditLogEvent.SaveLog -> {
                val uiState = addEditLogUiState.value
                val currentWeight = uiState.weight
                val currentLogId = uiState.currentLogId
                val currentDate = uiState.date

                viewModelScope.launch {
                    if (currentLogId != -1) {
                        val log = Log(
                            id = currentLogId,
                            weight = currentWeight,
                            date = currentDate
                        )
                        logUseCases.updateLog(
                            log = log
                        )
                        _newlyAddedId.value = currentLogId.toLong()
                    } else {
                        val log = Log(weight = currentWeight, date = currentDate)
                        _newlyAddedId.value = logUseCases.insertLog(
                            log = log
                        )
                    }
                }
            }

            is AddEditLogEvent.DatePickerDialogToggleVisibility -> {
                _addEditLogUiState.value = addEditLogUiState.value.copy(
                    datePickerDialogVisible = event.show
                )
            }

            is AddEditLogEvent.SetWeight -> {
                val uiState = addEditLogUiState.value
                _addEditLogUiState.value = uiState.copy(
                    weight = event.value.toWeight()
                )
            }

            AddEditLogEvent.DeleteLog -> {
                viewModelScope.launch {
                    logUseCases.deleteLogById(addEditLogUiState.value.currentLogId)
                }
            }
        }
    }
}