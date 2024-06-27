package com.leeweeder.weighttracker.data.repository

import com.leeweeder.weighttracker.data.datasource.IsoLocalDate
import com.leeweeder.weighttracker.domain.model.Log
import com.leeweeder.weighttracker.domain.repository.LogRepository
import com.leeweeder.weighttracker.util.Weight
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FakeLogRepository @Inject constructor() : LogRepository {
    private val logs = mutableListOf<Log>()

    override fun getLogs(): Flow<List<Log>> {
        return flow { emit(logs) }
    }

    override fun getFiveMostRecentLogs(): Flow<List<Log>> {
        return flow { emit(logs.take(5))}
    }

    override fun getOldestLogWeight(): Flow<Weight?> {
        TODO("Not yet implemented")
    }

    override suspend fun getLogById(id: Int): Log {
        return logs.find { it.id == id }!!
    }

    override suspend fun getLogByDate(date: IsoLocalDate): Log {
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