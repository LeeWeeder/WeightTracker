package com.leeweeder.weighttracker.data.datasource

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.leeweeder.weighttracker.domain.model.Log
import kotlinx.coroutines.flow.Flow

@Dao
interface LogDao {
    @Query("SELECT * FROM log ORDER BY date DESC")
    fun getLogs(): Flow<List<Log>>

    @Query("SELECT * FROM log ORDER BY date DESC LIMIT 5")
    fun getFiveMostRecentLogs(): Flow<List<Log>>

    @Query("SELECT * FROM log WHERE id = :id")
    suspend fun getLogById(id: Int): Log

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: Log): Long

    @Update
    suspend fun updateLog(log: Log)

    @Query("DELETE FROM log WHERE id = :id")
    suspend fun deleteLogById(id: Int)
}
