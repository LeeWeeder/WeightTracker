package com.leeweeder.weighttracker.ui.home

sealed class HomeEvent {
    data class DeleteLog(val logId: Int) : HomeEvent()
}
