package com.leeweeder.weighttracker.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leeweeder.weighttracker.domain.usecases.DataStoreUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnBoardingViewModel @Inject constructor(
    private val dataStoreUseCases: DataStoreUseCases
) : ViewModel() {
    fun onFinishOnBoarding (weight: Int) {
        setGoalWeight(weight)
        hideOnBoarding()
    }

    private fun setGoalWeight(weight: Int) {
        viewModelScope.launch {
            dataStoreUseCases.saveGoalWeight(weight)
        }
    }

    private fun hideOnBoarding() {
        viewModelScope.launch {
            dataStoreUseCases.saveShouldHideOnBoarding(true)
        }
    }
}