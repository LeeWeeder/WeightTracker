package com.leeweeder.weighttracker.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.leeweeder.weighttracker.R
import com.leeweeder.weighttracker.ui.LocalNavController
import com.leeweeder.weighttracker.ui.MainActivityViewModel
import com.leeweeder.weighttracker.ui.util.formatToTwoDecimalPlaces
import com.leeweeder.weighttracker.ui.util.getFormattedDate
import com.leeweeder.weighttracker.util.Screen
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineSpec
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.shader.color
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.common.shader.DynamicShader
import kotlin.math.absoluteValue

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = hiltViewModel(),
    mainActivityViewModel: MainActivityViewModel = hiltViewModel(),
    onNavigateToOnBoardingScreen: () -> Unit,
    fromOnBoarding: Boolean
) {
    val uiState = homeViewModel.homeUiState.value
    if (!fromOnBoarding) {
        when (mainActivityViewModel.shouldHideOnBoarding.value) {
            true -> {
                mainActivityViewModel.setIsLoading(false)
                HomeScreen(
                    uiState = uiState
                )
            }

            false -> {
                LaunchedEffect(Unit) {
                    onNavigateToOnBoardingScreen()
                }
            }

            null -> mainActivityViewModel.setIsLoading(true)
        }
    } else {
        HomeScreen(
            uiState = uiState
        )
    }
}

@Composable
fun HomeScreen(
    uiState: HomeUiState
) {
    val navController = LocalNavController.current

    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            if (uiState.fiveMostRecentLogs.isNotEmpty()) {
                ElevatedCard(
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.extraLarge
                ) {
                    CenterTopAppBar()
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .padding(bottom = 8.dp)
                    ) {
                        Column {
                            val mostRecentLog = uiState.mostRecentLog
                            Text(
                                text = "Current weight",
                                style = MaterialTheme.typography.labelLarge
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = mostRecentLog!!.date.getFormattedDate(),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.outline
                            )

                            Spacer(modifier = Modifier.height(16.dp))
                            Row {
                                Text(
                                    text = mostRecentLog.weight.displayValue,
                                    style = MaterialTheme.typography.displayLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Box(contentAlignment = Alignment.Center) {
                                    val difference = uiState.differenceFromPrevious
                                    Text(
                                        text = difference.formatToTwoDecimalPlaces() + "kg",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                        modifier = Modifier.offset(y = (6).dp)
                                    )
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.offset(y = 18.dp)
                                    ) {
                                        val indicatorColor = MaterialTheme.colorScheme.onSurface
                                        val neutralColor = MaterialTheme.colorScheme.surfaceVariant
                                        Icon(
                                            painter = painterResource(id = R.drawable.arrow_drop_up),
                                            contentDescription = "Weight increasing",
                                            modifier = Modifier.offset(y = (-3).dp),
                                            tint = if (difference > 0) {
                                                indicatorColor
                                            } else {
                                                neutralColor
                                            }
                                        )
                                        Icon(
                                            painter = painterResource(id = R.drawable.arrow_drop_down),
                                            contentDescription = "Weight decreasing",
                                            modifier = Modifier.offset(y = 3.dp),
                                            tint = if (difference < 0) {
                                                indicatorColor
                                            } else {
                                                neutralColor
                                            }
                                        )
                                    }
                                    Text(
                                        text = "kg", style = MaterialTheme.typography.labelSmall,
                                        modifier = Modifier.offset(y = 32.dp)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                                onClick = {},
                                modifier = Modifier.height(50.dp)
                            ) {
                                val modelProducer =
                                    remember { CartesianChartModelProducer.build() }
                                LaunchedEffect(uiState.fiveMostRecentLogs) {
                                    modelProducer.tryRunTransaction {
                                        val values = uiState.fiveMostRecentLogs.map {
                                            it.weight.value
                                        }.reversed()
                                        lineSeries {
                                            series(
                                                values
                                            )
                                        }
                                    }
                                }
                                CartesianChartHost(
                                    chart = rememberCartesianChart(
                                        rememberLineCartesianLayer(
                                            lines = listOf(
                                                rememberLineSpec(
                                                    backgroundShader = null,
                                                    shader = DynamicShader.color(MaterialTheme.colorScheme.tertiary)
                                                )
                                            )
                                        )
                                    ),
                                    modelProducer = modelProducer,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedCard(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {

                                }
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    val differenceFromGoal = uiState.differenceFromGoal
                                    if (differenceFromGoal != null) {
                                        Row(
                                            modifier = Modifier
                                                .padding(8.dp)
                                                .fillMaxWidth()
                                                .height(IntrinsicSize.Min),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Column(
                                                modifier = Modifier.fillMaxHeight(),
                                                verticalArrangement = Arrangement.SpaceEvenly
                                            ) {
                                                Text(
                                                    text = "Goal",
                                                    style = MaterialTheme.typography.labelSmall
                                                )
                                                Text(
                                                    text = "${
                                                        differenceFromGoal.absoluteValue.formatToTwoDecimalPlaces()
                                                    } kg " + if (differenceFromGoal > 0) {
                                                        "left"
                                                    } else {
                                                        "over"
                                                    },
                                                    style = MaterialTheme.typography.labelLarge.copy(
                                                        fontWeight = FontWeight.SemiBold
                                                    )
                                                )
                                            }
                                            Text(
                                                text = "${differenceFromGoal.formatToTwoDecimalPlaces()} kg",
                                                style = MaterialTheme.typography.displaySmall,
                                                color = MaterialTheme.colorScheme.secondary
                                            )
                                        }
                                    } else {
                                        Text(
                                            text = "Goal not set",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.outline
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                ElevatedCard(
                    modifier = Modifier
                        .padding(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Recent records",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        TextButton(onClick = {
                            navController.navigate(Screen.LogScreen.route)
                        }) {
                            Text(text = "See all")
                        }
                    }
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        uiState.fiveMostRecentLogs.forEach { log ->
                            ListItem(
                                headlineContent = {
                                    Text(
                                        text = log.date.getFormattedDate(),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                },
                                trailingContent = {
                                    Row(verticalAlignment = Alignment.Bottom) {
                                        Text(
                                            text = log.weight.displayValue,
                                            style = MaterialTheme.typography.titleLarge.copy(
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                        Text(
                                            text = " kg",
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                },
                                modifier = Modifier.clip(shape = MaterialTheme.shapes.medium),
                                colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest)
                            )
                        }
                    }
                }
            } else {
                NoData()
            }

        }
        AddWeightRecordFab(
            onClick = { navController.navigate(Screen.AddEditLogScreen.route) },
            modifier = Modifier
                .align(
                    Alignment.BottomEnd
                )
        )
    }
}

@Composable
internal fun AddWeightRecordFab(onClick: () -> Unit, modifier: Modifier = Modifier) {
    LargeFloatingActionButton(
        onClick = onClick,
        modifier = modifier.padding(16.dp),
        containerColor = MaterialTheme.colorScheme.primary
    ) {
        Icon(
            painter = painterResource(id = R.drawable.monitor_weight),
            contentDescription = "Add weight record",
            modifier = Modifier.size(FloatingActionButtonDefaults.LargeIconSize)
        )
    }
}

@Composable
internal fun NoData(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        CenterTopAppBar()
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.no_data),
                contentDescription = "No data available",
                alpha = 0.5f,
                modifier = Modifier.size(250.dp)
            )
            Text(text = "No data available", color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(4.dp))
            Row {
                Text(text = "Click ", color = MaterialTheme.colorScheme.outline)
                Icon(
                    painter = painterResource(id = R.drawable.monitor_weight),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Text(text = " to add one", color = MaterialTheme.colorScheme.outline)
            }
            Spacer(modifier = Modifier.height(128.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CenterTopAppBar() {
    CenterAlignedTopAppBar(title = { Text(text = "Weight Tracker") })
}