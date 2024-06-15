package com.leeweeder.weighttracker.ui.add_edit_log

sealed class AddEditLogEvent {
    data class DatePickerDialogToggleVisibility(val show: Boolean): AddEditLogEvent()
    data class TimePickerDialogToggleVisibility(val show: Boolean): AddEditLogEvent()
    data class SetDate(val millis: Long): AddEditLogEvent()
    data class SetTime(val millis: Long): AddEditLogEvent()
    data class SetWeight(val value: Double): AddEditLogEvent()
    data object SaveLog: AddEditLogEvent()
    data object DeleteLog: AddEditLogEvent()
}