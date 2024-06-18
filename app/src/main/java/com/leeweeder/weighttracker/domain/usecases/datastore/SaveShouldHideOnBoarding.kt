package com.leeweeder.weighttracker.domain.usecases.datastore

import com.leeweeder.weighttracker.domain.repository.DataStoreRepository

class SaveShouldHideOnBoarding(
    private val repository: DataStoreRepository
) {
    suspend operator fun invoke(shouldHideOnBoarding: Boolean) {
        repository.saveShouldHideOnBoarding(shouldHideOnBoarding)
    }
}