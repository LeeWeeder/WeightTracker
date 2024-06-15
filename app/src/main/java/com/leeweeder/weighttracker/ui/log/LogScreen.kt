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

import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.leeweeder.weighttracker.R
import com.leeweeder.weighttracker.ui.AddEditLogSharedViewModel
import com.leeweeder.weighttracker.ui.LocalNavController
import com.leeweeder.weighttracker.ui.util.getFormattedDate
import com.leeweeder.weighttracker.util.Screen
import kotlinx.coroutines.delay

@Composable
fun LogScreen(
    viewModel: LogViewModel = hiltViewModel(),
    sharedViewModel: AddEditLogSharedViewModel
) {
    val uiState = viewModel.logUiState.value
    val newlyAddedId = sharedViewModel.newlyAddedLogId.collectAsState().value
    LogScreen(
        uiState = uiState,
        newlyAddedId = newlyAddedId,
        onEvent = viewModel::onEvent,
        onEmphasisAnimationFinished = sharedViewModel::addNewLogId
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun LogScreen(
    uiState: LogUiState,
    newlyAddedId: Long?,
    onEvent: (LogEvent) -> Unit,
    onEmphasisAnimationFinished: (Long?) -> Unit
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
        Column {
            LazyColumn(contentPadding = paddingValues) {
                items(logs) { log ->
                    if (newlyAddedId != null && newlyAddedId.toInt() == log.id) {
                        var animationState by remember {
                            mutableStateOf(false)
                        }
                        val transition = updateTransition(
                            targetState = animationState,
                            label = "newly created or edited item"
                        )
                        val animateElevation by transition.animateDp(
                            label = "elevation",
                            transitionSpec = {
                                if (targetState) tween() else tween(delayMillis = 10)
                            }) { state ->
                            if (state) {
                                4.dp
                            } else {
                                0.dp
                            }
                        }

                        val animateCorner by transition.animateDp(label = "corner") { state ->
                            if (state) 24.dp else 8.dp
                        }

                        LaunchedEffect(key1 = Unit) {
                            delay(50)
                            animationState = true
                            delay(5000)
                            onEmphasisAnimationFinished(null)
                        }

                        LaunchedEffect(key1 = animationState) {
                            if (animationState) {
                                delay(1200)
                                animationState = false
                            }
                        }

                        Box(
                            modifier = Modifier
                                .padding(horizontal = animateElevation)
                        ) {
                            Surface(
                                tonalElevation = animateElevation,
                                modifier = Modifier.clip(
                                    RoundedCornerShape(animateCorner)
                                )
                            ) {
                                ListItem(
                                    headlineContent = {
                                        Text(
                                            text = log.date.getFormattedDate(),
                                            style = MaterialTheme.typography.labelMedium
                                        )
                                    },
                                    trailingContent = {
                                        Text(
                                            text = "${log.weight} kg",
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    },
                                    modifier = Modifier
                                        .clickable {
                                            navController.navigate(Screen.AddEditLogScreen.route + "?logId=${log.id}&logWeight=${log.weight}")
                                        },
                                    colors = ListItemDefaults.colors(
                                        containerColor = Color.Transparent
                                    )
                                )
                            }
                        }
                    } else {
                        ListItem(
                            headlineContent = {
                                Text(
                                    text = log.date.getFormattedDate(),
                                    style = MaterialTheme.typography.labelMedium
                                )
                            },
                            trailingContent = {
                                Text(
                                    text = "${log.weight} kg",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            },
                            modifier = Modifier
                                .clickable {
                                    navController.navigate(Screen.AddEditLogScreen.route + "?logId=${log.id}&logWeight=${log.weight}")
                                },
                            colors = ListItemDefaults.colors(
                                containerColor = Color.Transparent
                            )
                        )
                    }
                }
            }
        }
    }
}
