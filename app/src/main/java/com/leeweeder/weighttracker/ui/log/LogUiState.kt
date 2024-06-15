package com.leeweeder.weighttracker.ui.log

import com.leeweeder.weighttracker.domain.model.Log

data class LogUiState(
    val logs: List<Log> = emptyList()
)