package com.leeweeder.weighttracker.ui.add_edit_log

import com.leeweeder.weighttracker.util.Weight
import java.time.LocalDate

const val DEFAULT_LOG_ID = -1

data class AddEditLogUiState(
    val currentLogId: Int = DEFAULT_LOG_ID,
    val date: LocalDate = LocalDate.now(),
    val datePickerDialogVisible: Boolean = false,
    val weightState: WeightState = WeightState(Weight(0f)),
    val hasWeightChange: Boolean = false
)