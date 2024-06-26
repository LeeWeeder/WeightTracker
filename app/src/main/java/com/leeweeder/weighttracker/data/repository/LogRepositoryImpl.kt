package com.leeweeder.weighttracker.data.repository

import com.leeweeder.weighttracker.data.datasource.LogDao
import com.leeweeder.weighttracker.domain.model.Log
import com.leeweeder.weighttracker.domain.repository.LogRepository
import kotlinx.coroutines.flow.Flow

class LogRepositoryImpl(
    private val dao: LogDao
): LogRepository {
    override fun getLogs(): Flow<List<Log>> {
        return dao.getLogs()
    }

    override fun getFiveMostRecentLogs(): Flow<List<Log>> {
        return dao.getFiveMostRecentLogs()
    }

    override suspend fun getLogById(id: Int): Log {
        return dao.getLogById(id)
    }

    override suspend fun getLogByDate(millis: Long): Log {
        return dao.getLogByDate(millis)
    }

    override suspend fun insertLog(log: Log): Long {
        return dao.insertLog(log)
    }

    override suspend fun updateLog(log: Log) {
        dao.updateLog(log)
    }

    override suspend fun deleteLogById(id: Int) {
        dao.deleteLogById(id)
    }
}