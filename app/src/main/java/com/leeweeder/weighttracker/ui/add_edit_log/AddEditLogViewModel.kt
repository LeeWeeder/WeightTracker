package com.leeweeder.weighttracker.ui.add_edit_log

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leeweeder.weighttracker.domain.model.Log
import com.leeweeder.weighttracker.domain.usecases.LogUseCases
import com.leeweeder.weighttracker.ui.util.epochMillisToLocalDate
import com.leeweeder.weighttracker.util.Weight
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

const val LOG_ID_KEY = "logId"

@HiltViewModel
class AddEditLogViewModel @Inject constructor(
    private val logUseCases: LogUseCases,
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
                        val log = Log(id = currentLogId, weight = currentWeight, date = currentDate)
                        logUseCases.updateLog(
                            log = log
                        )
                        _newlyAddedId.value = currentLogId.toLong()
                    } else {
                        val log = Log(weight = currentWeight, date = currentDate)
                        _newlyAddedId.value = logUseCases.insertLog(
                            log = log
                        )
                        android.util.Log.d("newlyAddedId", _newlyAddedId.value.toString())
                    }
                }
            }

            is AddEditLogEvent.DatePickerDialogToggleVisibility -> {
                _addEditLogUiState.value = addEditLogUiState.value.copy(
                    datePickerDialogVisible = event.show
                )
            }

            is AddEditLogEvent.SetWeight -> {
                _addEditLogUiState.value = addEditLogUiState.value.copy(
                    weight = Weight(event.value)
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