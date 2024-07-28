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
import kotlinx.coroutines.launch
import java.time.LocalDate
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

    init {
        savedStateHandle.get<Int>(LOG_ID_KEY)?.let { logId ->
            if (logId != DEFAULT_LOG_ID) {
                viewModelScope.launch {
                    logUseCases.getLogById(logId).also { log ->
                        _addEditLogUiState.value = addEditLogUiState.value.copy(
                            currentLogId = log.id,
                            date = log.date,
                            weightState = log.weight.toWeightState()
                        )
                    }
                }
            } else {
                viewModelScope.launch {
                    logUseCases.getLogByDate(LocalDate.now()).also { log ->
                        if (log != null) {
                            _addEditLogUiState.value = addEditLogUiState.value.copy(
                                currentLogId = log.id,
                                weightState = log.weight.toWeightState()
                            )
                        } else {
                            logUseCases.getLatestLogs().also { listFlow ->
                                _addEditLogUiState.value = addEditLogUiState.value.copy(
                                    weightState = (listFlow.first().firstOrNull()?.weight
                                        ?: dataStoreUseCases.readGoalWeightState().first()
                                            .toWeight()).toWeightState()
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    fun onEvent(event: AddEditLogEvent) {
        when (event) {
            is AddEditLogEvent.SetDate -> {
                onEvent(AddEditLogEvent.DatePickerDialogToggleVisibility(show = false))
                val date = epochMillisToLocalDate(event.millis)
                viewModelScope.launch {
                    logUseCases.getLogByDate(date).also { log ->
                        if (log != null) {
                            _addEditLogUiState.value = addEditLogUiState.value.copy(
                                currentLogId = log.id,
                                weightState = log.weight.toWeightState()
                            )
                        } else {
                            _addEditLogUiState.value = addEditLogUiState.value.copy(
                                currentLogId = DEFAULT_LOG_ID
                            )
                        }
                    }
                }

                _addEditLogUiState.value = addEditLogUiState.value.copy(
                    date = date,
                    hasWeightChange = false
                )
            }

            AddEditLogEvent.SaveLog -> {
                val uiState = addEditLogUiState.value
                val currentWeight = uiState.weightState.potentialWeight
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
                    } else {
                        val log = Log(weight = currentWeight, date = currentDate)
                        logUseCases.insertLog(
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
                val weight = event.value.toWeight()
                val hasWeightChange = uiState.weightState.logWeight != weight
                _addEditLogUiState.value = uiState.copy(
                    hasWeightChange = hasWeightChange,
                    weightState = uiState.weightState.setPotentialWeight(weight)
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