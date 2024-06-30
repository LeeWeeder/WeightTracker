package com.leeweeder.weighttracker.ui.add_edit_log

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leeweeder.weighttracker.domain.model.Log
import com.leeweeder.weighttracker.domain.usecases.DataStoreUseCases
import com.leeweeder.weighttracker.domain.usecases.LogUseCases
import com.leeweeder.weighttracker.ui.util.epochMillisToLocalDate
import com.leeweeder.weighttracker.ui.util.toEpochMilli
import com.leeweeder.weighttracker.util.StartingWeightModel
import com.leeweeder.weighttracker.util.Weight
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
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
        viewModelScope.launch {
            dataStoreUseCases.readStartingWeightState().collectLatest { startingWeight ->
                _addEditLogUiState.value = addEditLogUiState.value.copy(
                    startingWeight = StartingWeightModel(
                        startingWeight.weight,
                        startingWeight.date,
                        startingWeight.wasGoalAchieved
                    )
                )
            }
        }
        viewModelScope.launch {
            dataStoreUseCases.readGoalWeightState().collectLatest { goalWeight ->
                _addEditLogUiState.value = addEditLogUiState.value.copy(
                    goalWeight = goalWeight
                )
            }
        }
        viewModelScope.launch {
            logUseCases.getFiveMostRecentLogs().collectLatest { logs ->
                _addEditLogUiState.value = addEditLogUiState.value.copy(
                    mostRecentLog = logs.firstOrNull()
                )
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
                val goalWeight = uiState.goalWeight!!
                val startingWeight = uiState.startingWeight

                viewModelScope.launch {
                    if (shouldUpdateStartingWeight(
                            startingWeight = startingWeight!!,
                            currentDate = currentDate,
                            goalWeight = goalWeight,
                            mostRecentLog = uiState.mostRecentLog,
                            currentWeight = currentWeight.value
                        )
                    ) {
                        dataStoreUseCases.saveStartingWeight(
                            StartingWeightModel(
                                weight = currentWeight.value,
                                date = currentDate.toEpochMilli()
                            )
                        )
                    }

                    if (currentWeight.value == goalWeight.toFloat()) dataStoreUseCases.saveStartingWeight(
                        startingWeight.copy(wasGoalAchieved = true)
                    )

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

fun shouldUpdateStartingWeight(
    startingWeight: StartingWeightModel,
    currentDate: LocalDate,
    goalWeight: Int,
    mostRecentLog: Log?,
    currentWeight: Float
): Boolean {
    val isFirstTime =
        startingWeight.weight == 0f && startingWeight.date == 0L && startingWeight.wasGoalAchieved == false
    val goalWasAchieved = startingWeight.wasGoalAchieved
    val startingWeightDate = startingWeight.date?.let { epochMillisToLocalDate(it) }
    val goalWeightFloat = goalWeight.toFloat()
    return isFirstTime
            || goalWasAchieved == true && ((mostRecentLog != null && mostRecentLog.weight.value == goalWeightFloat) || currentWeight == goalWeightFloat)
            || (currentDate <= startingWeightDate)
}