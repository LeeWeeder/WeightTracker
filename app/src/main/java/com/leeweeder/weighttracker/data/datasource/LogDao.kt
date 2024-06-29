package com.leeweeder.weighttracker.data.datasource

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.leeweeder.weighttracker.domain.model.Log
import com.leeweeder.weighttracker.util.Weight
import kotlinx.coroutines.flow.Flow

@Dao
interface LogDao {
    @Query("SELECT * FROM log ORDER BY date DESC")
    fun getLogs(): Flow<List<Log>>

    @Query("SELECT * FROM log ORDER BY date DESC LIMIT 5")
    fun getFiveMostRecentLogs(): Flow<List<Log>>

    @Query("SELECT weight FROM log ORDER BY date ASC LIMIT 1")
    fun getOldestLogWeight(): Flow<Weight?>

    @Query("SELECT * FROM log WHERE id = :id")
    suspend fun getLogById(id: Int): Log

    @Query("SELECT * FROM log WHERE date = :millis")
    suspend fun getLogByDate(millis: Long): Log

    @Query("SELECT CASE WHEN (SELECT MIN(date) FROM log) IS NULL OR :millis < (SELECT MIN(date) FROM log) THEN 1 ELSE 0 END")
    suspend fun isOlderThanAll(millis: Long): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: Log): Long

    @Update
    suspend fun updateLog(log: Log)

    @Query("DELETE FROM log WHERE id = :id")
    suspend fun deleteLogById(id: Int)
}
