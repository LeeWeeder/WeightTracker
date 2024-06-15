package com.leeweeder.weighttracker.ui

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class AddEditLogSharedViewModel @Inject constructor(): ViewModel() {
    private val _newlyAddedLogId = MutableStateFlow<Long?>(null)
    val newlyAddedLogId: StateFlow<Long?> = _newlyAddedLogId

    fun addNewLogId(id: Long?) {
        _newlyAddedLogId.value = id
        println("New id is now added: " + newlyAddedLogId.value)
    }
}