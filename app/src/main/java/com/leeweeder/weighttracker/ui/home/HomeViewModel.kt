package com.leeweeder.weighttracker.ui.home

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leeweeder.weighttracker.domain.usecases.DataStoreUseCases
import com.leeweeder.weighttracker.domain.usecases.LogUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val logUseCases: LogUseCases,
    private val dataStoreUseCases: DataStoreUseCases
) : ViewModel() {
    private val _homeUiState = mutableStateOf(HomeUiState())
    val homeUiState: State<HomeUiState> = _homeUiState

    private var getFiveMostRecentLogsJob: Job? = null

    init {
        getFiveMostRecentLogs()
        viewModelScope.launch {
            dataStoreUseCases.readGoalWeightState().collectLatest { goalWeight ->
                _homeUiState.value = homeUiState.value.copy(
                    goalWeight = goalWeight
                )
            }
        }
    }

    fun setGoalWeight(value: Int) {
        viewModelScope.launch {
            dataStoreUseCases.saveGoalWeight(value)
        }
    }

    private fun getFiveMostRecentLogs() {
        getFiveMostRecentLogsJob?.cancel()
        getFiveMostRecentLogsJob = logUseCases.getFiveMostRecentLogs()
            .onEach { logs ->
                _homeUiState.value = homeUiState.value.copy(
                    fiveMostRecentLogs = logs
                )
            }
            .launchIn(viewModelScope)
    }
}