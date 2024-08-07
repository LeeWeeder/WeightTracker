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

package com.leeweeder.weighttracker.domain.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.leeweeder.weighttracker.ui.components.DeleteLogRequest
import com.leeweeder.weighttracker.util.Weight
import java.time.LocalDate

@Entity(indices = [Index(value = ["date"], unique = true)])
data class Log(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    val weight: Weight,
    val date: LocalDate
)

fun Log.toDeleteLogRequest(): DeleteLogRequest = DeleteLogRequest(this.id, this.date)
