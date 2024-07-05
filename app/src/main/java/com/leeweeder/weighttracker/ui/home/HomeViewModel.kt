package com.leeweeder.weighttracker.ui.home

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leeweeder.weighttracker.domain.usecases.DataStoreUseCases
import com.leeweeder.weighttracker.domain.usecases.LogUseCases
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.common.data.ExtraStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

val goalWeightKey = ExtraStore.Key<Float>()
val daysOfWeeksWithValuesKey = ExtraStore.Key<Set<Float>>()
val mostRecentLogDayOfTheWeekKey = ExtraStore.Key<Float>()

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val logUseCases: LogUseCases,
    private val dataStoreUseCases: DataStoreUseCases
) : ViewModel() {
    private val _homeUiState = mutableStateOf(HomeUiState())
    val homeUiState: State<HomeUiState> = _homeUiState

    private var getFiveMostRecentLogsJob: Job? = null

    private val _modelProducer = CartesianChartModelProducer()
    val modelProducer
        get() = _modelProducer

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

    fun observeMostRecentLogsAndGoalWeight() {
        viewModelScope.launch {
            snapshotFlow {
                homeUiState.value.fiveMostRecentLogs
            }
                .onEach { loadLineChart() }
                .launchIn(viewModelScope)

            snapshotFlow { homeUiState.value.goalWeight }
                .onEach { loadLineChart() }
                .launchIn(viewModelScope)
        }
    }

    private fun loadLineChart() {
        val data = homeUiState.value.fiveMostRecentLogs.reversed()
        if (data.isEmpty()) return

        viewModelScope.launch {
            modelProducer.runTransaction {
                lineSeries {
                    series(
                        x = data.map { it.date.dayOfWeek.value },
                        y = data.map { it.weight.value }
                    )
                }
                extras { extraStore ->
                    extraStore[goalWeightKey] = homeUiState.value.goalWeight.toFloat()
                    extraStore[daysOfWeeksWithValuesKey] =
                        data.map {
                            it.date.dayOfWeek.value.toFloat()
                        }.toSet()
                    val mostRecentLog = homeUiState.value.mostRecentLog
                    if (mostRecentLog != null)
                        extraStore[mostRecentLogDayOfTheWeekKey] =
                            mostRecentLog.date.dayOfWeek.value.toFloat()
                }
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