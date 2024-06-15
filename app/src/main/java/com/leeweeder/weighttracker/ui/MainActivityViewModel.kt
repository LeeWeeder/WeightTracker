package com.leeweeder.weighttracker.ui

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leeweeder.weighttracker.domain.usecases.DataStoreUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val dataStoreUseCases: DataStoreUseCases
) : ViewModel() {
    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private val _showOnBoarding = mutableStateOf<Boolean?>(null)
    val showOnBoarding: State<Boolean?> = _showOnBoarding

    init {
        viewModelScope.launch {
            dataStoreUseCases.readOnBoardingState().collectLatest {
                _showOnBoarding.value = it
            }
        }
    }

    fun setIsLoading(value: Boolean) {
        _isLoading.value = value
    }
}