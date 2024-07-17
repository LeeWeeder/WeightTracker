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

package com.leeweeder.weighttracker.ui.log

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.leeweeder.weighttracker.R
import com.leeweeder.weighttracker.ui.LocalNavController
import com.leeweeder.weighttracker.ui.components.LogItem
import com.leeweeder.weighttracker.ui.util.format
import com.leeweeder.weighttracker.util.Screen

@Composable
fun LogScreen(
    viewModel: LogViewModel = hiltViewModel()
) {
    val uiState = viewModel.logUiState.value
    LogScreen(
        uiState = uiState,
        onEvent = viewModel::onEvent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun LogScreen(
    uiState: LogUiState,
    onEvent: (LogEvent) -> Unit
) {
    val navController = LocalNavController.current
    val logs = uiState.logs
    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = {
                    Text(text = "Log weight")
                },
                icon = {
                    Icon(painter = painterResource(id = R.drawable.add), contentDescription = null)
                },
                onClick = {
                    navController.navigate(Screen.AddEditLogScreen.route)
                })
        },
        topBar = {
            LargeTopAppBar(title = {
                Text(text = "Weight Tracker")
            })
        }
    ) { paddingValues ->
        LazyColumn(contentPadding = paddingValues) {
            items(logs) { log ->
                LogItem(dateText = log.date.format("MMM d"), log = log, relativeDateEnabled = false)
            }
        }
    }
}
