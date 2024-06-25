package com.leeweeder.weighttracker.data.repository

import com.leeweeder.weighttracker.data.datasource.IsoLocalDate
import com.leeweeder.weighttracker.data.datasource.LogDao
import com.leeweeder.weighttracker.domain.model.Log
import com.leeweeder.weighttracker.domain.repository.LogRepository
import com.leeweeder.weighttracker.util.Weight
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

    override fun getOldestLogWeight(): Flow<Weight?> {
        return dao.getOldestLogWeight()
    }

    override suspend fun getLogById(id: Int): Log {
        return dao.getLogById(id)
    }

    override suspend fun getLogByDate(date: IsoLocalDate): Log {
        return dao.getLogByDate(date)
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