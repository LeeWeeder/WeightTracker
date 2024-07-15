package com.leeweeder.weighttracker.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.leeweeder.weighttracker.R
import com.leeweeder.weighttracker.ui.LocalNavController
import com.leeweeder.weighttracker.ui.MainActivityViewModel
import com.leeweeder.weighttracker.ui.home.components.GoalScreenDialog
import com.leeweeder.weighttracker.ui.home.components.ThisWeekCard
import com.leeweeder.weighttracker.ui.util.format
import com.leeweeder.weighttracker.ui.util.formatToOneDecimalPlace
import com.leeweeder.weighttracker.ui.util.model.LineChartData
import com.leeweeder.weighttracker.ui.util.model.WeekRange
import com.leeweeder.weighttracker.util.Screen
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = hiltViewModel(),
    mainActivityViewModel: MainActivityViewModel = hiltViewModel(),
    onNavigateToOnBoardingScreen: () -> Unit,
    fromOnBoarding: Boolean
) {
    val uiState = homeViewModel.homeUiState.value
    val setGoalWeight = homeViewModel::setGoalWeight
    val observeThisWeekLogsAndGoalWeight = homeViewModel::observeThisWeekLogsAndGoalWeight
    val modelProducer = homeViewModel.modelProducer

    val homeScreen = @Composable {
        HomeScreen(
            uiState = uiState,
            onWeightGoalSet = setGoalWeight,
            observeThisWeekLogsAndGoalWeight = observeThisWeekLogsAndGoalWeight,
            modelProducer = modelProducer
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
    onWeightGoalSet: (weight: Int) -> Unit,
    observeThisWeekLogsAndGoalWeight: () -> Unit,
    modelProducer: CartesianChartModelProducer
) {
    val goalScreenDialogVisible = remember {
        mutableStateOf(false)
    }
    val navController = LocalNavController.current
    GoalScreenDialog(
        visible = goalScreenDialogVisible.value,
        initialValue = uiState.goalWeight,
        onDismissRequest = {
            goalScreenDialogVisible.value = false
        }) { weight ->
        onWeightGoalSet(weight)
    }
    Scaffold(
        floatingActionButton = {
            AddWeightRecordFab(onClick = { navController.navigate(Screen.AddEditLogScreen.route) })
        },
        topBar = { WeightTrackerTopAppBar() }
    ) {
        HomeScreenContent(
            uiState = uiState,
            paddingValues = PaddingValues(
                top = it.calculateTopPadding(),
                bottom = it.calculateBottomPadding()
            ),
            showGoalScreen = {
                goalScreenDialogVisible.value = true
            },
            observeThisWeekLogsAndGoalWeight = observeThisWeekLogsAndGoalWeight,
            modelProducer = modelProducer
        )
    }
}

@Composable
fun HomeScreenContent(
    uiState: HomeUiState,
    paddingValues: PaddingValues,
    showGoalScreen: () -> Unit,
    observeThisWeekLogsAndGoalWeight: () -> Unit,
    modelProducer: CartesianChartModelProducer
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(paddingValues)
            .safeContentPadding()
            .consumeWindowInsets(paddingValues)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier.fillMaxWidth()
        ) {
            CurrentWeight(
                uiState = uiState
            )
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)) {
                    val differenceFromGoal = uiState.mostRecentDifferenceFromGoal
                    Column(
                        modifier = Modifier
                            .padding(14.dp)
                    ) {
                        Text(text = "Progress", style = MaterialTheme.typography.labelSmall)
                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            val textStyle = MaterialTheme.typography.bodySmall
                            if (differenceFromGoal == 0f) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.check_small),
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Text(text = "Maintain weight!", style = textStyle)
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
                                        ?: "No data to track",
                                    style = textStyle
                                )
                            }
                        }
                    }
                }
                Card(
                    shape = MaterialTheme.shapes.extraLarge,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                    onClick = showGoalScreen
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Goal",
                            style = MaterialTheme.typography.labelSmall
                        )
                        Text(
                            text = uiState.goalWeight.toString(),
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Spacer(modifier = Modifier.height(24.dp))
        val daysOfWeek = uiState.daysOfWeek
        ThisWeekCard(
            weekRange = WeekRange(start = daysOfWeek.first(), end = daysOfWeek.last()),
            lineChartData = LineChartData(
                modelProducer = modelProducer,
                dataObserver = observeThisWeekLogsAndGoalWeight
            ),
            logs = uiState.logsForThisWeek
        )
    }
}

@Composable
private fun AddWeightRecordFab(onClick: () -> Unit) {
    LargeFloatingActionButton(
        onClick = onClick,
        containerColor = MaterialTheme.colorScheme.primary
    ) {
        Icon(
            painter = painterResource(id = R.drawable.add_weight_record),
            contentDescription = "Add weight record",
            modifier = Modifier.size(FloatingActionButtonDefaults.LargeIconSize)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeightTrackerTopAppBar() {
    CenterAlignedTopAppBar(
        title = { Text(text = "Weight Tracker") }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrentWeight(uiState: HomeUiState) {
    val mostRecentLog = uiState.mostRecentLog
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = CircleShape
                )
                .size(158.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(bottom = 2.dp),
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
                style = MaterialTheme.typography.displayLarge.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
            )
            val difference = uiState.mostRecentDifferenceFromPrevious
            val differenceText =
                difference?.let {
                    it.formatToOneDecimalPlace(showPlusSign = true) + " kg"
                }
                    ?: "-"
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 4.dp)
            ) {
                val scope = rememberCoroutineScope()
                val tooltipState = rememberTooltipState(isPersistent = true)
                TooltipBox(
                    positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                    tooltip = {
                        PlainTooltip {
                            Text(text = "The change compared to the previous record.")
                        }
                    },
                    state = tooltipState
                ) {
                    Text(
                        text = differenceText,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.clickable(interactionSource = remember {
                            MutableInteractionSource()
                        }, indication = null, onClick = { scope.launch { tooltipState.show() } })
                    )
                }
            }
        }
    }
}
