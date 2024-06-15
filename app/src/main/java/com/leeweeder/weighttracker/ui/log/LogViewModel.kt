/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.leeweeder.weighttracker.ui.log

import com.leeweeder.weighttracker.domain.usecases.LogUseCases
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LogViewModel @Inject constructor(
    private val logUseCases: LogUseCases
) : ViewModel() {

    private val _logUiState = mutableStateOf(LogUiState())
    val logUiState: State<LogUiState> = _logUiState

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private var getLogsJob: Job? = null

    init {
        getLogs()
    }


    fun onEvent(event: LogEvent) {
        when (event) {
            is LogEvent.UpsertLog -> {
                viewModelScope.launch {
                    try {
                        logUseCases.insertLog(
                            event.log
                        )
                        _eventFlow.emit(UiEvent.SaveLog)
                    } catch (e: Exception) {
                        _eventFlow.emit(UiEvent.ShowMessage("Couldn't save log"))
                    }
                }
            }
        }
    }

    private fun getLogs() {
        getLogsJob?.cancel()
        getLogsJob = logUseCases.getLogs()
            .onEach { logs ->
                _logUiState.value = logUiState.value.copy(
                    logs = logs
                )
            }
            .launchIn(viewModelScope)
    }

    sealed class UiEvent {
        data object SaveLog : UiEvent()
        data class ShowMessage(val string: String): UiEvent()
    }
}