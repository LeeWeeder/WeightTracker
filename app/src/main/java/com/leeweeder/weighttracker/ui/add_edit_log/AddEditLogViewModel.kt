package com.leeweeder.weighttracker.ui.add_edit_log

import android.icu.util.Calendar
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leeweeder.weighttracker.domain.model.Log
import com.leeweeder.weighttracker.domain.usecases.LogUseCases
import com.leeweeder.weighttracker.ui.util.getDatePickerCompatibleFormat
import com.leeweeder.weighttracker.ui.util.getFormattedDate
import com.leeweeder.weighttracker.util.Weight
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.Instant
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
                            date = log.date.getDatePickerCompatibleFormat(),
                            time = log.date,
                            dateIsSetByUser = true,
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
                    dateIsSetByUser = true,
                    date = Instant.ofEpochMilli(event.millis)
                )
            }

            is AddEditLogEvent.SetTime -> {
                onEvent(AddEditLogEvent.TimePickerDialogToggleVisibility(show = false))
                _addEditLogUiState.value = addEditLogUiState.value.copy(
                    dateIsSetByUser = true,
                    time = Instant.ofEpochMilli(event.millis)
                )
            }

            AddEditLogEvent.SaveLog -> {
                val weight = addEditLogUiState.value.weight
                val currentLogId = addEditLogUiState.value.currentLogId

                if (addEditLogUiState.value.dateIsSetByUser) {
                    val date = combineDateAndTime()
                    if (currentLogId != -1) {
                        val log = Log(id = currentLogId, weight = weight, date = date)
                        viewModelScope.launch {
                            logUseCases.updateLog(
                                log = log
                            )
                            _newlyAddedId.value = currentLogId.toLong()
                        }
                    } else {
                        val log = Log(weight = weight, date = date)
                        viewModelScope.launch {
                            _newlyAddedId.value = logUseCases.insertLog(
                                log = log
                            )
                        }
                    }
                } else {
                    val log = Log(weight = weight)
                    viewModelScope.launch {
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

            is AddEditLogEvent.TimePickerDialogToggleVisibility -> {
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

    private fun combineDateAndTime(): Instant {
        val date = addEditLogUiState.value.date
        val time = addEditLogUiState.value.time
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, date.getFormattedDate("yyyy").toInt())
        calendar.set(Calendar.MONTH, date.getFormattedDate("M").toInt() - 1)
        calendar.set(Calendar.DATE, date.getFormattedDate("d").toInt())
        calendar.set(Calendar.HOUR, time.getFormattedDate("h").toInt())
        calendar.set(Calendar.MINUTE, time.getFormattedDate("m").toInt())

        if (time.getFormattedDate("a").equals("AM", ignoreCase = true)) {
            calendar.set(Calendar.AM_PM, Calendar.AM)
        } else {
            calendar.set(Calendar.AM_PM, Calendar.PM)
        }

        return Instant.ofEpochMilli(calendar.timeInMillis)
    }
}