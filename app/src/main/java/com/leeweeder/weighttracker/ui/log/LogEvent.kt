package com.leeweeder.weighttracker.ui.log

import com.leeweeder.weighttracker.domain.model.Log

sealed class LogEvent {
    data class UpsertLog(val log: Log): LogEvent()
    data class DeleteLog(val id: Int): LogEvent()
}