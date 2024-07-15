package com.leeweeder.weighttracker.ui.home.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.leeweeder.weighttracker.domain.model.Log
import com.leeweeder.weighttracker.ui.components.LineChart
import com.leeweeder.weighttracker.ui.components.LogItem
import com.leeweeder.weighttracker.ui.components.NoData
import com.leeweeder.weighttracker.ui.components.SectionLabel
import com.leeweeder.weighttracker.ui.util.format
import com.leeweeder.weighttracker.ui.util.model.LineChartData
import com.leeweeder.weighttracker.ui.util.model.WeekRange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ThisWeekCard(weekRange: WeekRange, lineChartData: LineChartData, logs: List<Log>) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
    ) {
        val segmentedButtonItems = SegmentedButtonItems.entries
        val selectedIndex = remember {
            mutableIntStateOf(SegmentedButtonItems.CHART.ordinal)
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 8.dp, top = 8.dp, bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            SectionLabel(text = "This week")
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = weekRange.format("MMM d"),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                val minWeight = logs.minOfOrNull { it.weight.value }
                val maxWeight = logs.maxOfOrNull { it.weight.value }
                val weightRange =
                    if (minWeight == null && maxWeight == null) "No data" else if (minWeight == maxWeight) "$minWeight kg" else "$minWeight–$maxWeight kg"
                Text(
                    text = weightRange,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
            TextButton(onClick = { }) {
                Text(text = "See all")
            }
        }
        SingleChoiceSegmentedButtonRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            segmentedButtonItems.forEachIndexed { index, tabItem ->
                SegmentedButton(
                    selected = index == selectedIndex.intValue,
                    onClick = { selectedIndex.intValue = index },
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = segmentedButtonItems.size
                    ),
                    label = {
                        Text(
                            tabItem.name.lowercase()
                                .replaceFirstChar { it.uppercaseChar() })
                    }
                )
            }
        }

        AnimatedContent(
            targetState = segmentedButtonItems[selectedIndex.intValue],
            label = "Tab content"
        ) { targetState ->
            when (targetState) {
                SegmentedButtonItems.CHART -> {
                    LineChart(
                        modelProducer = lineChartData.modelProducer,
                        dataObserver = lineChartData.dataObserver,
                        modifier = Modifier
                            .padding(bottom = 16.dp)
                            .padding(horizontal = 12.dp)
                    )
                }

                SegmentedButtonItems.LOGS -> {
                    val sortedLogs = logs.sortedBy { it.date.dayOfWeek }
                    if (sortedLogs.isEmpty()) {
                        NoData(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 32.dp)
                        )
                    } else {
                        Column(modifier = Modifier.padding(top = 8.dp, bottom = 12.dp)) {
                            sortedLogs.forEach { log ->
                                LogItem(
                                    dateText = log.date.format(
                                        pattern = "EEEE, MMM d"
                                    ), log = log
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private enum class SegmentedButtonItems {
    CHART,
    LOGS
}