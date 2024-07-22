package com.leeweeder.weighttracker.ui.home.components

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.text.Layout
import android.text.SpannableStringBuilder
import android.text.style.ReplacementSpan
import android.text.style.StyleSpan
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.leeweeder.weighttracker.domain.model.Log
import com.leeweeder.weighttracker.ui.LocalNavController
import com.leeweeder.weighttracker.ui.components.LineChart
import com.leeweeder.weighttracker.ui.components.LogItem
import com.leeweeder.weighttracker.ui.components.NoData
import com.leeweeder.weighttracker.ui.components.SectionLabel
import com.leeweeder.weighttracker.ui.home.daysOfWeeksWithValuesKey
import com.leeweeder.weighttracker.ui.home.mostRecentLogDayOfTheWeekKey
import com.leeweeder.weighttracker.ui.home.xToDateMapKey
import com.leeweeder.weighttracker.ui.util.format
import com.leeweeder.weighttracker.ui.util.model.LineChartData
import com.leeweeder.weighttracker.ui.util.model.WeekRange
import com.leeweeder.weighttracker.util.Screen
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisLineComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisTickComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottomAxis
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.of
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.common.Dimensions
import com.patrykandpatrick.vico.core.common.shape.Shape
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ThisWeekCard(
    weekRange: WeekRange,
    lineChartData: LineChartData,
    logs: List<Log>
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
    ) {
        val segmentedButtonItems = SegmentedButtonItems.entries
        val selectedIndex = remember {
            mutableIntStateOf(SegmentedButtonItems.CHART.ordinal)
        }
        val navController = LocalNavController.current
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
            TextButton(onClick = {
                navController.navigate(Screen.LogScreen.route)
            }) {
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
                    ThisWeekCardLineChart(
                        modelProducer = lineChartData.modelProducer,
                        dataObserver = lineChartData.dataObserver
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
                                    ), log = log,
                                    onClick = {
                                        navController.navigate(Screen.AddEditLogScreen.createRoute(logId = log.id))
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ThisWeekCardLineChart(
    modelProducer: CartesianChartModelProducer,
    dataObserver: () -> Unit
) {
    class CircleBackgroundSpan(
        private val backgroundColor: Color?,
        private val textColor: Color,
        private val borderColor: Color?
    ) :
        ReplacementSpan() {
        override fun getSize(
            paint: Paint,
            text: CharSequence,
            start: Int,
            end: Int,
            fm: Paint.FontMetricsInt?
        ): Int {
            return paint.measureText(text, start, end).toInt()
        }

        override fun draw(
            canvas: Canvas,
            text: CharSequence,
            start: Int,
            end: Int,
            x: Float,
            top: Int,
            y: Int,
            bottom: Int,
            paint: Paint
        ) {
            val textHeight = bottom - top
            val radius = textHeight / 1f

            val textWidth = paint.measureText(text, start, end)
            val centerX = x + textWidth / 2

            if (backgroundColor != null) {
                paint.color = backgroundColor.toArgb()
                canvas.drawCircle(centerX, (top + bottom) / 2f, radius, paint)
            }

            paint.color = textColor.toArgb()

            canvas.drawText(text, start, end, x, y.toFloat(), paint)

            if (borderColor != null) {
                paint.color = borderColor.toArgb()
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = 2f
                canvas.drawCircle(centerX, (top + bottom) / 2f, radius, paint)
            }
        }
    }

    fun setSpan(
        text: String,
        backgroundColor: Color?,
        textColor: Color,
        borderColor: Color? = null
    ): SpannableStringBuilder {
        val spannable = SpannableStringBuilder(text)
        val start = 0
        val end = text.length
        spannable.setSpan(
            CircleBackgroundSpan(
                backgroundColor = backgroundColor,
                textColor = textColor,
                borderColor = borderColor
            ),
            start,
            end,
            SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannable.setSpan(StyleSpan(Typeface.BOLD), start, end, 0)
        return spannable
    }

    data class DayOfTheWeekIndicatorProperty(
        val backgroundColor: Color?,
        val textColor: Color,
        val borderColor: Color?
    )

    @Composable
    fun rememberBottomAxisValueFormatter(): CartesianValueFormatter {
        val colorScheme = MaterialTheme.colorScheme
        return remember {
            CartesianValueFormatter { x, chartValues, _ ->
                val extraStore = chartValues.model.extraStore
                val (backgroundColor, textColor, borderColor) = if (x == extraStore[mostRecentLogDayOfTheWeekKey])
                    DayOfTheWeekIndicatorProperty(
                        backgroundColor = colorScheme.secondary,
                        textColor = colorScheme.onSecondary,
                        borderColor = colorScheme.secondary
                    )
                else if (extraStore[daysOfWeeksWithValuesKey].contains(x))
                    DayOfTheWeekIndicatorProperty(
                        backgroundColor = colorScheme.secondaryContainer,
                        textColor = colorScheme.onSecondaryContainer,
                        borderColor = colorScheme.secondaryContainer
                    )
                else if (x == LocalDate.now().toEpochDay().toFloat())
                    DayOfTheWeekIndicatorProperty(
                        backgroundColor = null,
                        textColor = colorScheme.secondary,
                        borderColor = colorScheme.secondary
                    )
                else
                    DayOfTheWeekIndicatorProperty(
                        backgroundColor = null,
                        textColor = colorScheme.secondary,
                        borderColor = colorScheme.secondaryContainer
                    )
                setSpan(
                    (chartValues.model.extraStore[xToDateMapKey][x] ?: LocalDate.ofEpochDay(
                        x.toLong()
                    )).format("E").first().toString(),
                    backgroundColor,
                    textColor,
                    borderColor
                )
            }
        }
    }

    val bottomAxis = rememberBottomAxis(
        label = rememberTextComponent(
            margins = Dimensions.of(top = 10.dp, bottom = 10.dp),
            textAlignment = Layout.Alignment.ALIGN_CENTER
        ),
        line = rememberAxisLineComponent(
            thickness = 2.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        ),
        tick = rememberAxisTickComponent(
            color = MaterialTheme.colorScheme.outlineVariant,
            shape = Shape.Rectangle,
            thickness = 2.dp
        ),
        guideline = null,
        itemPlacer = remember {
            HorizontalAxis.ItemPlacer.default(addExtremeLabelPadding = true)
        },
        valueFormatter = rememberBottomAxisValueFormatter()
    )

    LineChart(
        modelProducer = modelProducer,
        dataObserver = dataObserver,
        modifier = Modifier
            .padding(bottom = 16.dp)
            .padding(horizontal = 12.dp),
        bottomAxis = bottomAxis
    )
}

private enum class SegmentedButtonItems {
    CHART,
    LOGS
}