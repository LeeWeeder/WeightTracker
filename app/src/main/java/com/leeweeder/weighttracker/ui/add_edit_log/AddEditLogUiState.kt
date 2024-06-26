package com.leeweeder.weighttracker.ui.add_edit_log

import com.leeweeder.weighttracker.util.Weight
import java.time.LocalDate

data class AddEditLogUiState(
    val currentLogId: Int = -1,
    val date: LocalDate = LocalDate.now(),
    val datePickerDialogVisible: Boolean = false,
    val weight: Weight = Weight(0f)
)