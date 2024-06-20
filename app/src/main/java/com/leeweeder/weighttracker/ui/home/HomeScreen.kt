package com.leeweeder.weighttracker.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.leeweeder.weighttracker.R
import com.leeweeder.weighttracker.domain.model.Log
import com.leeweeder.weighttracker.ui.LocalNavController
import com.leeweeder.weighttracker.ui.MainActivityViewModel
import com.leeweeder.weighttracker.ui.util.format
import com.leeweeder.weighttracker.ui.util.formatToTwoDecimalPlaces
import com.leeweeder.weighttracker.util.Screen
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.chart.line.lineSpec
import com.patrykandpatrick.vico.core.chart.values.AxisValuesOverrider
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entryOf
import kotlin.math.absoluteValue

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = hiltViewModel(),
    mainActivityViewModel: MainActivityViewModel = hiltViewModel(),
    onNavigateToOnBoardingScreen: () -> Unit,
    fromOnBoarding: Boolean
) {
    val navController = LocalNavController.current
    val uiState = homeViewModel.homeUiState.value
    val modelProducer = homeViewModel.modelProducer
    val onNavigateToLogScreen = { navController.navigate(Screen.LogScreen.route) }
    val onNavigateToAddEditLogScreen = { navController.navigate(Screen.AddEditLogScreen.route) }

    val homeScreen = @Composable {
        HomeScreen(
            uiState = uiState,
            modelProducer = modelProducer,
            onNavigateToLogScreen = onNavigateToLogScreen,
            onNavigateToAddEditLogScreen = onNavigateToAddEditLogScreen
        )
    }

    if (!fromOnBoarding) {
        when (mainActivityViewModel.shouldHideOnBoarding.value) {
            true -> {
                mainActivityViewModel.setIsLoading(false)
                homeScreen()
            }

            false -> {
                LaunchedEffect(Unit) {
                    onNavigateToOnBoardingScreen()
                }
            }

            null -> mainActivityViewModel.setIsLoading(true)
        }
    } else {
        homeScreen()
    }
}

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    modelProducer: ChartEntryModelProducer,
    onNavigateToLogScreen: () -> Unit = {},
    onNavigateToAddEditLogScreen: () -> Unit = {}
) {
    Scaffold(
        floatingActionButton = {
            AddWeightRecordFab(onClick = onNavigateToAddEditLogScreen)
        },
        topBar = {
            TopAppCard(uiState = uiState)
        }
    ) {
        HomeScreenContent(
            uiState = uiState,
            modelProducer = modelProducer,
            onNavigateToLogScreen = onNavigateToLogScreen,
            modifier = Modifier.padding(it)
        )
    }
}

@Composable
fun HomeScreenContent(
    uiState: HomeUiState,
    modelProducer: ChartEntryModelProducer,
    onNavigateToLogScreen: () -> Unit,
    modifier: Modifier
) {
    Column(modifier = modifier) {
        LineChart(data = uiState.fiveMostRecentLogs, modelProducer = modelProducer)
        RecentRecord(uiState = uiState, onNavigateToLogScreen = onNavigateToLogScreen)
    }
}

@Composable
fun RecentRecord(uiState: HomeUiState, onNavigateToLogScreen: () -> Unit) {
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
            SectionLabel(title = "Recent records")
            TextButton(onClick = onNavigateToLogScreen) {
                Text(text = "See all")
            }
        }
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (uiState.fiveMostRecentLogs.isEmpty()) {
                NoData()
            } else {
                uiState.fiveMostRecentLogs.forEach { log ->
                    ListItem(
                        headlineContent = {
                            Text(
                                text = log.date.format("EEE, MM d", true),
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
    }
}

@Composable
internal fun AddWeightRecordFab(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = onClick
    ) {
        Icon(
            painter = painterResource(id = R.drawable.monitor_weight),
            contentDescription = "Add weight record"
        )
    }
}

@Composable
internal fun NoData() {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeightTrackerTopAppBar() {
    CenterAlignedTopAppBar(title = { Text(text = "Weight Tracker") })
}

@Composable
fun TopAppCard(uiState: HomeUiState) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge
    ) {
        WeightTrackerTopAppBar()
        CurrentWeight(uiState = uiState, modifier = Modifier.padding(horizontal = 16.dp))
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            GoalCard(goalWeight = uiState.goalWeight, modifier = Modifier.weight(1f))
            RemainingCard(
                differenceFromGoal = uiState.differenceFromGoal,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun GoalCard(goalWeight: Double?, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        onClick = {}
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            SectionLabel(title = "Goal")
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = goalWeight?.let { it.formatToTwoDecimalPlaces(showTrailingZero = false) + " kg" }
                    ?: "-",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

@Composable
fun RemainingCard(differenceFromGoal: Double?, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            SectionLabel(title = "Remaining")
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = differenceFromGoal?.absoluteValue?.let {
                    it.formatToTwoDecimalPlaces() + "kg " + if (differenceFromGoal > 0) {
                        "left"
                    } else {
                        "over"
                    }
                }
                    ?: "-",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

@Composable
fun LineChart(data: List<Log>, modelProducer: ChartEntryModelProducer) {
    ElevatedCard(
        modifier = Modifier
            .padding(vertical = 16.dp, horizontal = 8.dp),
        onClick = {}
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            SectionLabel(title = "Trend")
            Spacer(modifier = Modifier.height(8.dp))

            LaunchedEffect(data) {
                val entries = List(data.size) { entryOf(it, data[it].weight.value) }
                modelProducer.setEntries(entries)
            }

            val lineColor = MaterialTheme.colorScheme.primary

            Chart(chart = lineChart(
                remember {
                    listOf(lineSpec(lineColor, lineBackgroundShader = null))
                },
                axisValuesOverrider = AxisValuesOverrider.adaptiveYValues(1f, true)
            ),
                chartModelProducer = modelProducer,
                startAxis = rememberStartAxis(),
                bottomAxis = rememberBottomAxis()
            )
        }
    }
}

@Composable
fun CurrentWeight(uiState: HomeUiState, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        SectionLabel(title = "Current weight")
        Spacer(modifier = Modifier.height(8.dp))
        val mostRecentLog = uiState.mostRecentLog

        Text(
            text = mostRecentLog?.date?.format("EEE, MM d", true) ?: "-",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outline
        )

        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = mostRecentLog?.weight?.displayValue ?: "-",
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
            )
            TrendIndicator(uiState = uiState, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun SectionLabel(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.secondary,
        modifier = modifier
    )
}

@Composable
private fun TrendIndicator(uiState: HomeUiState, modifier: Modifier = Modifier) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
        val difference = uiState.differenceFromPrevious
        Text(
            text = difference?.let { it.formatToTwoDecimalPlaces() + " kg" } ?: "-",
            style = MaterialTheme.typography.labelSmall
        )
        Box(
            contentAlignment = Alignment.Center
        ) {
            val indicatorColor = MaterialTheme.colorScheme.onSurface
            val neutralColor =
                MaterialTheme.colorScheme.surfaceVariant
            Icon(
                painter = painterResource(id = R.drawable.arrow_drop_up),
                contentDescription = "Weight increasing",
                modifier = Modifier.offset(y = (-3).dp),
                tint = if (difference != null && difference > 0) {
                    indicatorColor
                } else {
                    neutralColor
                }
            )
            Icon(
                painter = painterResource(id = R.drawable.arrow_drop_down),
                contentDescription = "Weight decreasing",
                modifier = Modifier.offset(y = 3.dp),
                tint = if (difference != null && difference < 0) {
                    indicatorColor
                } else {
                    neutralColor
                }
            )
        }
    }
}