package com.leeweeder.weighttracker.data.repository

import com.leeweeder.weighttracker.domain.model.Log
import com.leeweeder.weighttracker.domain.repository.LogRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FakeLogRepository @Inject constructor() : LogRepository {
    private val logs = mutableListOf<Log>()

    override fun getLogs(): Flow<List<Log>> {
        return flow { emit(logs) }
    }

    override fun getLogsAroundDate(epochDay: Long, padding: Int): Flow<List<Log>> {
        TODO("Not yet implemented")
    }

    override suspend fun getLogById(id: Int): Log {
        return logs.find { it.id == id }!!
    }

    override suspend fun getLogByDate(epochDay: Long): Log {
        TODO("Not yet implemented")
    }

    override suspend fun insertLog(log: Log): Long {
        logs.add(log)
        return logs.indexOf(log).toLong()
    }

    override suspend fun updateLog(log: Log) {
        logs[log.id] = log
    }

    override suspend fun deleteLogById(id: Int) {
        logs.removeAt(id)
    }
}