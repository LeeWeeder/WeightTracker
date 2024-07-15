package com.leeweeder.weighttracker.domain.usecases

import com.leeweeder.weighttracker.domain.usecases.log.DeleteLogById
import com.leeweeder.weighttracker.domain.usecases.log.GetLogByDate
import com.leeweeder.weighttracker.domain.usecases.log.GetLogById
import com.leeweeder.weighttracker.domain.usecases.log.GetLogs
import com.leeweeder.weighttracker.domain.usecases.log.GetLogsForThisWeek
import com.leeweeder.weighttracker.domain.usecases.log.InsertLog
import com.leeweeder.weighttracker.domain.usecases.log.UpdateLog

data class LogUseCases(
    val getLogs: GetLogs,
    val getLogById: GetLogById,
    val getLogByDate: GetLogByDate,
    val insertLog: InsertLog,
    val updateLog: UpdateLog,
    val deleteLogById: DeleteLogById,
    val getLogsForThisWeek: GetLogsForThisWeek
)
