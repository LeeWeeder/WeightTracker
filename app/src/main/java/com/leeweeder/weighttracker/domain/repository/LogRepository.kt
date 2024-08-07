/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.leeweeder.weighttracker.domain.repository

import com.leeweeder.weighttracker.domain.model.Log
import kotlinx.coroutines.flow.Flow

interface LogRepository {
    fun getLogs(): Flow<List<Log>>

    fun getLatestLogs(number: Int): Flow<List<Log>>

    fun getLogsAroundDate(epochDay: Long, padding: Int): Flow<List<Log>>

    suspend fun getLogById(id: Int): Log

    suspend fun getLogByDate(epochDay: Long): Log?

    suspend fun insertLog(log: Log): Long

    suspend fun updateLog(log: Log)

    suspend fun deleteLogById(id: Int)
}