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

    override fun getLogsAroundDate(epochDay: Long, padding: Int): Flow<List<Log>> {
        return dao.getLogsAroundDate(epochDay, padding)
    }

    override suspend fun getLogById(id: Int): Log {
        return dao.getLogById(id)
    }

    override suspend fun getLogByDate(epochDay: Long): Log {
        return dao.getLogByDate(epochDay)
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