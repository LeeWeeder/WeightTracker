package com.leeweeder.weighttracker.ui.home.components

import android.graphics.Canvas
import android.graphics.Paint
import android.text.Layout
import android.text.SpannableStringBuilder
import android.text.style.ReplacementSpan
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.leeweeder.weighttracker.ui.home.NoData
import com.leeweeder.weighttracker.ui.home.daysOfWeeksWithValuesKey
import com.leeweeder.weighttracker.ui.home.goalWeightKey
import com.leeweeder.weighttracker.ui.home.mostRecentLogDayOfTheWeekKey
import com.leeweeder.weighttracker.ui.home.util.weightTrackerValueOverrider
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisGuidelineComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisLineComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisTickComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottomAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberEndAxis
import com.patrykandpatrick.vico.compose.cartesian.decoration.rememberHorizontalLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineSpec
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.of
import com.patrykandpatrick.vico.compose.common.shader.color
import com.patrykandpatrick.vico.compose.common.shape.dashed
import com.patrykandpatrick.vico.core.cartesian.HorizontalLayout
import com.patrykandpatrick.vico.core.cartesian.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.cartesian.data.AxisValueOverrider
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.common.Dimensions
import com.patrykandpatrick.vico.core.common.HorizontalPosition
import com.patrykandpatrick.vico.core.common.VerticalPosition
import com.patrykandpatrick.vico.core.common.shader.DynamicShader
import com.patrykandpatrick.vico.core.common.shape.Shape
import java.time.LocalDate


@Composable
fun LineChart(
    mostRecentLogDayOfTheWeek: Float?,
    modelProducer: CartesianChartModelProducer,
    observeFiveMostRecentLogsAndGoalWeight: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        observeFiveMostRecentLogsAndGoalWeight()
    }

    val marker = rememberMarker()

    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val onSecondaryColor = MaterialTheme.colorScheme.onSecondary
    val secondaryContainerColor = MaterialTheme.colorScheme.secondaryContainer
    val onSecondaryContainerColor = MaterialTheme.colorScheme.onSecondaryContainer

    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberLineCartesianLayer(
                lines = listOf(
                    rememberLineSpec(
                        shader = DynamicShader.color(primaryColor),
                        thickness = 2.dp,
                        backgroundShader = null,
                        point = rememberShapeComponent(
                            shape = Shape.Pill,
                            color = primaryColor
                        ),
                        pointSize = 3.5.dp
                    )
                ),
                axisValueOverrider = remember {
                    AxisValueOverrider.weightTrackerValueOverrider()
                }
            ),
            endAxis = rememberEndAxis(
                axis = null,
                tick = null,
                guideline = rememberAxisGuidelineComponent(
                    shape = Shape.Rectangle,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    thickness = 1.2.dp
                ),
                itemPlacer = AxisItemPlacer.Vertical.count(count = { 3 })
            ),
            bottomAxis = rememberBottomAxis(
                label = rememberTextComponent(
                    margins = Dimensions.of(top = 10.dp, bottom = 10.dp),
                    textAlignment = Layout.Alignment.ALIGN_CENTER
                ),
                axis = rememberAxisLineComponent(
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
                    AxisItemPlacer.Horizontal.default(addExtremeLabelPadding = true)
                },
                valueFormatter = remember {
                    val days = listOf("S", "M", "T", "W", "T", "F", "S")
                    CartesianValueFormatter { x, chartValues, _ ->
                        val extraStore = chartValues.model.extraStore
                        val (backgroundColor, textColor, borderColor) = if (x == extraStore[mostRecentLogDayOfTheWeekKey])
                            Triple(
                                secondaryColor,
                                onSecondaryColor,
                                secondaryColor
                            )
                        else if (extraStore[daysOfWeeksWithValuesKey].contains(x))
                            Triple(
                                secondaryContainerColor,
                                onSecondaryContainerColor,
                                secondaryContainerColor
                            )
                        else if (x == LocalDate.now().dayOfWeek.value.toFloat())
                            Triple(
                                null,
                                secondaryColor,
                                secondaryColor
                            )
                        else
                            Triple(
                                null,
                                secondaryColor,
                                secondaryContainerColor
                            )
                        setSpan(
                            days[x.toInt() % days.size],
                            backgroundColor,
                            textColor,
                            borderColor
                        )
                    }
                }
            ),
            decorations = listOf(
                rememberHorizontalLine(
                    y = { it[goalWeightKey] },
                    line = rememberLineComponent(
                        color = MaterialTheme.colorScheme.primary.copy(0.6f),
                        shape = Shape.dashed(shape = Shape.Rectangle, 5.dp, 2.5.dp),
                        margins = Dimensions.of(start = 20.dp),
                        thickness = 0.8.dp
                    ),
                    labelComponent = rememberTextComponent(
                        color = MaterialTheme.colorScheme.primary
                    ),
                    horizontalLabelPosition = HorizontalPosition.Start,
                    verticalLabelPosition = VerticalPosition.Center
                )
            ),
            persistentMarkers = mostRecentLogDayOfTheWeek?.let { mapOf(it to marker) }
        ), modelProducer = modelProducer,
        marker = marker,
        horizontalLayout = HorizontalLayout.FullWidth(
            scalableStartPaddingDp = 15f,
            scalableEndPaddingDp = 15f
        ),
        modifier = modifier,
        zoomState = rememberVicoZoomState(zoomEnabled = false),
        placeholder = {
            NoData()
        }
    )
}

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
            paint.strokeWidth = 3f
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
    spannable.setSpan(
        CircleBackgroundSpan(backgroundColor, textColor, borderColor),
        0,
        text.length,
        SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    return spannable
}