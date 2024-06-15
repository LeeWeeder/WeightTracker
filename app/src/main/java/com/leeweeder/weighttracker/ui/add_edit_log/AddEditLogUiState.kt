package com.leeweeder.weighttracker.ui.add_edit_log

import com.leeweeder.weighttracker.ui.util.getDatePickerCompatibleFormat
import com.leeweeder.weighttracker.util.Weight
import java.time.Instant

data class AddEditLogUiState(
    val currentLogId: Int = -1,
    val time: Instant = Instant.now(),
    val date: Instant = Instant.now().getDatePickerCompatibleFormat(),
    val dateIsSetByUser: Boolean = false,
    val datePickerDialogVisible: Boolean = false,
    val timePickerDialogVisible: Boolean = false,
    val weight: Weight = Weight(0.0)
)