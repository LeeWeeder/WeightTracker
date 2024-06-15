package com.leeweeder.weighttracker.domain.usecases.log

import com.leeweeder.weighttracker.domain.repository.LogRepository

class DeleteLogById(
    private val repository: LogRepository
) {
    suspend operator fun invoke(id: Int) {
        repository.deleteLogById(id)
    }
}