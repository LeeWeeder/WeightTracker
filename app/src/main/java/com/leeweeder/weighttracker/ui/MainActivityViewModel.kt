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

    private val _shouldHideOnBoarding = mutableStateOf<Boolean?>(null)
    val shouldHideOnBoarding: State<Boolean?> = _shouldHideOnBoarding

    init {
        viewModelScope.launch {
            dataStoreUseCases.readOnBoardingState().collectLatest {
                _shouldHideOnBoarding.value = it
                println(shouldHideOnBoarding)
            }
        }
    }

    fun setIsLoading(value: Boolean) {
        _isLoading.value = value
    }
}