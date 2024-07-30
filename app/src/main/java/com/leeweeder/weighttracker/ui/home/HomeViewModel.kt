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
import java.time.LocalDate
import javax.inject.Inject

val goalWeightKey = ExtraStore.Key<Float>()
val daysOfWeeksWithValuesKey = ExtraStore.Key<Set<Float>>()
val currentLogDayOfTheWeek = ExtraStore.Key<Float>()
val xToDateMapKey = ExtraStore.Key<Map<Float, LocalDate>>()
val daysOfTheWeek = ExtraStore.Key<List<LocalDate>>()

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val logUseCases: LogUseCases,
    private val dataStoreUseCases: DataStoreUseCases
) : ViewModel() {
    private val _homeUiState = mutableStateOf(HomeUiState())
    val homeUiState: State<HomeUiState> = _homeUiState

    private var getLogsForWeekJob: Job? = null
    private var getLatestLogPairJob: Job? = null

    private val _modelProducer = CartesianChartModelProducer()
    val modelProducer
        get() = _modelProducer

    init {
        getLogsForWeek()
        getLatestLogPair()
        viewModelScope.launch {
            dataStoreUseCases.readGoalWeightState().collectLatest { goalWeight ->
                _homeUiState.value = homeUiState.value.copy(
                    goalWeight = goalWeight
                )
            }
        }
    }

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.DeleteLog -> {
                viewModelScope.launch {
                    logUseCases.deleteLogById(event.logId)
                }
            }
        }
    }

    fun observeThisWeekLogsAndGoalWeight() {
        viewModelScope.launch {
            snapshotFlow {
                homeUiState.value.logsForThisWeek
            }
                .onEach { loadLineChart() }
                .launchIn(viewModelScope)

            snapshotFlow { homeUiState.value.goalWeight }
                .onEach { loadLineChart() }
                .launchIn(viewModelScope)
        }
    }

    private fun loadLineChart() {
        val originalData =
            homeUiState.value.logsForThisWeek.also { if (it.isEmpty()) return }

        viewModelScope.launch {
            val precedingDay =
                logUseCases.getLogByDate(homeUiState.value.daysOfWeek.first().minusDays(1))

            val data = originalData.associate {
                it.date to it.weight.value
            }.toMutableMap().also {
                if (precedingDay != null) it[precedingDay.date] = precedingDay.weight.value
            }.toSortedMap().toMap()

            val xToDates = data.keys.associateBy { it.toEpochDay().toFloat() }

            modelProducer.runTransaction {
                lineSeries {
                    series(
                        x = xToDates.keys,
                        y = data.values
                    )
                }
                extras { extraStore ->
                    extraStore[goalWeightKey] = homeUiState.value.goalWeight.toFloat()
                    extraStore[daysOfWeeksWithValuesKey] =
                        originalData.map {
                            it.date.toEpochDay().toFloat()
                        }.toSet()
                    val currentLog = homeUiState.value.latestLogPair.currentLog
                    if (currentLog != null)
                        extraStore[currentLogDayOfTheWeek] =
                            currentLog.date.toEpochDay().toFloat()
                    extraStore[xToDateMapKey] = xToDates
                    extraStore[daysOfTheWeek] = homeUiState.value.daysOfWeek
                }
            }
        }
    }

    fun setGoalWeight(value: Int) {
        viewModelScope.launch {
            dataStoreUseCases.saveGoalWeight(value)
        }
    }

    private fun getLogsForWeek() {
        getLogsForWeekJob?.cancel()
        getLogsForWeekJob = logUseCases.getLogsForThisWeek(homeUiState.value.today)
            .onEach { logs ->
                _homeUiState.value = homeUiState.value.copy(
                    logsForThisWeek = logs
                )
            }
            .launchIn(viewModelScope)
    }

    private fun getLatestLogPair() {
        getLatestLogPairJob?.cancel()
        getLatestLogPairJob = logUseCases.getLatestLogs(2).onEach { logs ->
            _homeUiState.value = homeUiState.value.copy(
                latestLogPair = LatestLogPair(logs.firstOrNull(), logs.lastOrNull())
            )
        }.launchIn(viewModelScope)
    }
}