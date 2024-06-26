package com.leeweeder.weighttracker.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.leeweeder.weighttracker.R
import com.leeweeder.weighttracker.domain.model.Log
import com.leeweeder.weighttracker.ui.LocalNavController
import com.leeweeder.weighttracker.ui.MainActivityViewModel
import com.leeweeder.weighttracker.ui.home.components.GoalScreenDialog
import com.leeweeder.weighttracker.ui.util.format
import com.leeweeder.weighttracker.ui.util.formatToOneDecimalPlace
import com.leeweeder.weighttracker.util.Screen
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.chart.line.lineSpec
import com.patrykandpatrick.vico.core.chart.values.AxisValuesOverrider
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
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
    val onWeightGoalSet = homeViewModel::setGoalWeight

    val homeScreen = @Composable {
        HomeScreen(
            uiState = uiState,
            modelProducer = modelProducer,
            onNavigateToLogScreen = onNavigateToLogScreen,
            onNavigateToAddEditLogScreen = onNavigateToAddEditLogScreen,
            onWeightGoalSet = onWeightGoalSet
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
    onNavigateToAddEditLogScreen: () -> Unit = {},
    onWeightGoalSet: (weight: Int) -> Unit
) {
    val goalScreenDialogVisible = remember {
        mutableStateOf(false)
    }
    GoalScreenDialog(
        visible = goalScreenDialogVisible.value,
        initialValue = uiState.goalWeight,
        onDismissRequest = {
            goalScreenDialogVisible.value = false
        }) { weight ->
        onWeightGoalSet(weight)
    }

    val fabHeight = remember {
        mutableStateOf(0.dp)
    }
    Scaffold(
        floatingActionButton = {
            AddWeightRecordFab(onClick = onNavigateToAddEditLogScreen, onHeightSet = {
                fabHeight.value = it
            })
        }
    ) {
        HomeScreenContent(
            uiState = uiState,
            modelProducer = modelProducer,
            onNavigateToLogScreen = onNavigateToLogScreen,
            paddingValues = PaddingValues(
                top = it.calculateTopPadding(),
                bottom = it.calculateBottomPadding() + fabHeight.value + 32.dp
            ),
            showGoalScreen = {
                goalScreenDialogVisible.value = true
            }
        )
    }
}

@Composable
fun HomeScreenContent(
    uiState: HomeUiState,
    modelProducer: ChartEntryModelProducer,
    paddingValues: PaddingValues,
    onNavigateToLogScreen: () -> Unit,
    showGoalScreen: () -> Unit
) {
    LazyColumn(
        contentPadding = paddingValues,
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .consumeWindowInsets(paddingValues)
    ) {
        item { WeightTrackerTopAppBar() }
        item {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier.fillMaxWidth()
            ) {
                CurrentWeight(
                    uiState = uiState
                )
                Card(
                    shape = MaterialTheme.shapes.extraLarge,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                    onClick = showGoalScreen
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        SectionLabel(
                            title = "Goal",
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                        Text(
                            text = uiState.goalWeight.toString(),
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                }
            }
        }
        item {
            val differenceFromGoal = uiState.mostRecentDifferenceFromGoal
            Spacer(modifier = Modifier.height(16.dp))
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                val textStyle = MaterialTheme.typography.labelMedium
                val color = MaterialTheme.colorScheme.secondary
                if (differenceFromGoal?.toInt() == 0) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.check_small),
                            contentDescription = null,
                            tint = color
                        )
                        Text(text = "Stay consistent!", style = textStyle, color = color)
                    }
                } else {
                    Text(
                        text = differenceFromGoal?.absoluteValue?.let {
                            it.formatToOneDecimalPlace() + " kg " + if (differenceFromGoal > 0) {
                                "left to gain"
                            } else {
                                "left to lose"
                            }
                        }
                            ?: "-",
                        style = textStyle,
                        color = color
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
        item {
            LineChart(data = uiState.fiveMostRecentLogs, modelProducer = modelProducer)
        }
        item {
            RecentRecord(uiState = uiState, onNavigateToLogScreen = onNavigateToLogScreen)
        }
    }
}

@Composable
private fun RecentRecord(uiState: HomeUiState, onNavigateToLogScreen: () -> Unit) {
    ElevatedCard {
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
            modifier = Modifier.padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            if (uiState.fiveMostRecentLogs.isEmpty()) {
                NoData()
            } else {
                uiState.fiveMostRecentLogs.forEach { log ->
                    ListItem(
                        headlineContent = {
                            Text(
                                text = log.date.format("EEE, MMM d, yyyy", true),
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
                        modifier = Modifier
                            .clip(shape = MaterialTheme.shapes.large)
                            .clickable { /*TODO*/ }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun AddWeightRecordFab(onClick: () -> Unit, onHeightSet: (Dp) -> Unit = {}) {
    val density = LocalDensity.current
    FloatingActionButton(
        onClick = onClick,
        modifier = Modifier.onGloballyPositioned {
            onHeightSet(with(density) {
                it.size.height.toDp()
            })
        }
    ) {
        Icon(
            painter = painterResource(id = R.drawable.add_weight_record),
            contentDescription = "Add weight record"
        )
    }
}

@Composable
private fun NoData() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.no_data),
            contentDescription = "No data available",
            alpha = 0.3f,
            modifier = Modifier.size(128.dp)
        )
        val style = MaterialTheme.typography.bodyMedium
        Text(
            text = "No data available",
            color = MaterialTheme.colorScheme.outlineVariant,
            style = style
        )
        Spacer(modifier = Modifier.height(2.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Click ", color = MaterialTheme.colorScheme.outline, style = style)
            Icon(
                painter = painterResource(id = R.drawable.add_weight_record),
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Text(text = " to add one", color = MaterialTheme.colorScheme.outline, style = style)
        }
        Spacer(modifier = Modifier.height(40.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeightTrackerTopAppBar() {
    CenterAlignedTopAppBar(
        title = { Text(text = "Weight Tracker") }
    )
}

@Composable
fun LineChart(data: List<Log>, modelProducer: ChartEntryModelProducer) {
    ElevatedCard(
        modifier = Modifier
            .padding(vertical = 16.dp),
        onClick = {}
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            SectionLabel(title = "Trend")
            Spacer(modifier = Modifier.height(8.dp))

            val lineColor = MaterialTheme.colorScheme.primary

            Chart(
                chart = lineChart(
                    remember {
                        listOf(lineSpec(lineColor, lineBackgroundShader = null))
                    },
                    axisValuesOverrider = AxisValuesOverrider.adaptiveYValues(1f, true)
                ),
                chartModelProducer = modelProducer,
                startAxis = rememberStartAxis(),
                bottomAxis = rememberBottomAxis(),
                placeholder = {
                    NoData()
                }
            )
        }
    }
}

@Composable
fun CurrentWeight(uiState: HomeUiState) {
    val mostRecentLog = uiState.mostRecentLog
    Column(
        horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
            .padding(top = 32.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = CircleShape
                )
                .size(170.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(bottom = 6.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Text(
                    text = mostRecentLog?.date?.format("EEE, MMM d, yyyy", true) ?: "-",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Text(
                text = mostRecentLog?.weight?.displayValue ?: "-",
                style = MaterialTheme.typography.displayLarge.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .offset(y = (-8).dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Spacer(modifier = Modifier.height(10.dp))
                TrendIndicator(
                    uiState = uiState
                )
            }
        }
    }
}

@Composable
private fun SectionLabel(
    title: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.secondary
) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = color,
        modifier = modifier
    )
}

@Composable
private fun TrendIndicator(uiState: HomeUiState) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        val difference = uiState.mostRecentDifferenceFromPrevious
        Text(
            text = difference?.let { it.formatToOneDecimalPlace(showPlusSign = true) + " kg" }
                ?: "-",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Box(
            contentAlignment = Alignment.Center
        ) {
            val indicatorColor = MaterialTheme.colorScheme.onPrimaryContainer
            val neutralColor =
                MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f)
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
