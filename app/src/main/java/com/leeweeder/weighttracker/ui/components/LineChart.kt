package com.leeweeder.weighttracker.ui.components

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.text.Layout
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.ReplacementSpan
import android.text.style.StyleSpan
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.leeweeder.weighttracker.ui.home.daysOfTheWeek
import com.leeweeder.weighttracker.ui.home.daysOfWeeksWithValuesKey
import com.leeweeder.weighttracker.ui.home.goalWeightKey
import com.leeweeder.weighttracker.ui.home.mostRecentLogDayOfTheWeekKey
import com.leeweeder.weighttracker.ui.home.xToDateMapKey
import com.leeweeder.weighttracker.ui.util.format
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisGuidelineComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisLineComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisTickComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottomAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberEndAxis
import com.patrykandpatrick.vico.compose.cartesian.decoration.rememberHorizontalLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberPoint
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.component.fixed
import com.patrykandpatrick.vico.compose.common.component.rememberLayeredComponent
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.of
import com.patrykandpatrick.vico.compose.common.shader.color
import com.patrykandpatrick.vico.compose.common.shape.dashed
import com.patrykandpatrick.vico.compose.common.shape.markerCornered
import com.patrykandpatrick.vico.core.cartesian.CartesianChart
import com.patrykandpatrick.vico.core.cartesian.CartesianDrawContext
import com.patrykandpatrick.vico.core.cartesian.CartesianMeasureContext
import com.patrykandpatrick.vico.core.cartesian.HorizontalDimensions
import com.patrykandpatrick.vico.core.cartesian.HorizontalLayout
import com.patrykandpatrick.vico.core.cartesian.Insets
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.AxisValueOverrider
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModel
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.cartesian.marker.CartesianMarker
import com.patrykandpatrick.vico.core.cartesian.marker.CartesianMarkerValueFormatter
import com.patrykandpatrick.vico.core.cartesian.marker.DefaultCartesianMarker
import com.patrykandpatrick.vico.core.cartesian.marker.LineCartesianLayerMarkerTarget
import com.patrykandpatrick.vico.core.common.Dimensions
import com.patrykandpatrick.vico.core.common.HorizontalPosition
import com.patrykandpatrick.vico.core.common.VerticalPosition
import com.patrykandpatrick.vico.core.common.component.TextComponent
import com.patrykandpatrick.vico.core.common.data.ExtraStore
import com.patrykandpatrick.vico.core.common.shader.DynamicShader
import com.patrykandpatrick.vico.core.common.shape.Corner
import com.patrykandpatrick.vico.core.common.shape.Shape
import java.text.DecimalFormat
import java.time.LocalDate
import kotlin.math.roundToInt

@Composable
fun LineChart(
    modelProducer: CartesianChartModelProducer,
    dataObserver: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        dataObserver()
    }

    CartesianChartHost(
        chart = rememberLineChart(),
        modelProducer = modelProducer,
        modifier = modifier,
        zoomState = rememberVicoZoomState(zoomEnabled = false),
        placeholder = {
            NoData(modifier = Modifier.fillMaxSize())
        },
        scrollState = rememberVicoScrollState(scrollEnabled = false)
    )
}

private class CircleBackgroundSpan(
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

private fun setSpan(
    text: String,
    backgroundColor: Color?,
    textColor: Color,
    borderColor: Color? = null
): SpannableStringBuilder {
    val spannable = SpannableStringBuilder(text)
    val start = 0
    val end = text.length
    spannable.setSpan(
        CircleBackgroundSpan(backgroundColor, textColor, borderColor),
        start,
        end,
        SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    spannable.setSpan(StyleSpan(Typeface.BOLD), start, end, 0)
    return spannable
}

@Composable
private fun rememberLineChart(): CartesianChart {
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val onSecondaryColor = MaterialTheme.colorScheme.onSecondary
    val secondaryContainerColor = MaterialTheme.colorScheme.secondaryContainer
    val onSecondaryContainerColor = MaterialTheme.colorScheme.onSecondaryContainer

    val weightLine = rememberLine(
        shader = DynamicShader.color(primaryColor),
        thickness = 3.dp,
        backgroundShader = null,
        pointProvider = LineCartesianLayer.PointProvider.single(
            rememberPoint(
                component = rememberShapeComponent(
                    shape = Shape.Pill,
                    color = primaryColor
                ),
                size = 3.5.dp
            )
        )
    )

    return rememberCartesianChart(
        rememberLineCartesianLayer(
            lineProvider = LineCartesianLayer.LineProvider.series(
                weightLine
            ),
            axisValueOverrider = remember {
                AxisValueOverrider.weightTrackerValueOverrider()
            }
        ),
        endAxis = rememberEndAxis(
            line = null,
            tick = null,
            guideline = rememberAxisGuidelineComponent(
                shape = Shape.Rectangle,
                color = MaterialTheme.colorScheme.outlineVariant,
                thickness = 1.2.dp
            ),
            itemPlacer = VerticalAxis.ItemPlacer.count(count = { 3 })
        ),
        bottomAxis = rememberBottomAxis(
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
            valueFormatter = remember {
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
                        (chartValues.model.extraStore[xToDateMapKey][x] ?: LocalDate.ofEpochDay(
                            x.toLong()
                        )).format("E").first().toString(),
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
                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f),
                    shape = Shape.dashed(shape = Shape.Pill, dashLength = 5.dp, gapLength = 3.dp),
                    thickness = 1.5.dp
                ),
                labelComponent = rememberTextComponent(
                    color = MaterialTheme.colorScheme.secondary,
                    padding = Dimensions.of(horizontal = 4.dp),
                    margins = Dimensions.of(end = (-20).dp)
                ),
                horizontalLabelPosition = HorizontalPosition.End,
                verticalLabelPosition = VerticalPosition.Center
            )
        ),
        horizontalLayout = HorizontalLayout.FullWidth(
            scalableStartPaddingDp = 15f,
            scalableEndPaddingDp = 15f
        ),
        marker = rememberMarker()
    )
}

private fun AxisValueOverrider.Companion.weightTrackerValueOverrider(): AxisValueOverrider {
    return object : AxisValueOverrider {
        override fun getMinY(minY: Float, maxY: Float, extraStore: ExtraStore): Float {
            val goalWeight = extraStore[goalWeightKey]
            val newMinY = minOf(minY, goalWeight)
            val newMaxY = maxOf(maxY, goalWeight)
            val rangeTenPercent = (newMaxY - newMinY) * 0.1f
            val threshold = if (rangeTenPercent.roundToInt() == 0) 2f else rangeTenPercent
            return if (newMinY - threshold.roundToInt()
                    .toFloat() <= 0
            ) 0f else newMinY - threshold.roundToInt().toFloat()
        }

        override fun getMaxY(minY: Float, maxY: Float, extraStore: ExtraStore): Float {
            val goalWeight = extraStore[goalWeightKey]
            val newMaxY = maxOf(maxY, goalWeight)
            val newMinY = minOf(minY, goalWeight)
            val rangeTenPercent = (newMaxY - newMinY) * 0.1f
            val threshold = if (rangeTenPercent.roundToInt() == 0) 1f else rangeTenPercent
            return (newMaxY + threshold.roundToInt().toFloat())
        }

        override fun getMinX(minX: Float, maxX: Float, extraStore: ExtraStore): Float {
            return extraStore[daysOfTheWeek].minOf { it.toEpochDay().toFloat() }
        }

        override fun getMaxX(minX: Float, maxX: Float, extraStore: ExtraStore): Float {
            return extraStore[daysOfTheWeek].maxOf { it.toEpochDay().toFloat() }
        }
    }
}

private class WeightTrackerMarkerValueFormatter(
    private val color: Int
) : CartesianMarkerValueFormatter {
    // There is only one chart. Edit as further feature is needed
    private fun SpannableStringBuilder.append(y: Float, color: Int) {
        val decimalFormat = DecimalFormat("#.##;−#.##")
        appendCompat(
            decimalFormat.format(y) + " kg",
            ForegroundColorSpan(color),
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE,
        )
    }

    override fun format(
        context: CartesianDrawContext,
        targets: List<CartesianMarker.Target>,
    ): CharSequence =
        SpannableStringBuilder().apply {
            targets.forEach { target ->
                if (target is LineCartesianLayerMarkerTarget) {
                    target.points.forEach { point ->
                        append(point.entry.y, color)
                        append(
                            " on " + LocalDate.ofEpochDay(point.entry.x.toLong()).format("MMM d")
                        )
                        setSpan(StyleSpan(Typeface.BOLD), 0, this.length, 0)
                    }
                }
            }
        }
}


private fun SpannableStringBuilder.appendCompat(
    text: CharSequence,
    what: Any,
    flags: Int,
): SpannableStringBuilder = append(text, what, flags)

@Composable
private fun rememberMarker(): CartesianMarker {
    val labelBackgroundShape = Shape.markerCornered(Corner.FullyRounded)
    val context = LocalContext.current
    val bgColor = Color(context.getColor(android.R.color.system_neutral1_10))
    val color = Color(context.getColor(android.R.color.system_accent1_400))
    val labelBackground =
        rememberShapeComponent(bgColor, labelBackgroundShape)
            .setShadow(
                radius = LABEL_BACKGROUND_SHADOW_RADIUS_DP,
                dy = LABEL_BACKGROUND_SHADOW_DY_DP
            )
    val label =
        rememberTextComponent(
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
            background = labelBackground,
            padding = Dimensions.of(12.dp, 8.dp),
            textAlignment = Layout.Alignment.ALIGN_CENTER,
            minWidth = TextComponent.MinWidth.fixed(40.dp),
        )
    val indicatorFrontComponent =
        rememberShapeComponent(bgColor, Shape.Pill)
    val indicatorCenterComponent = rememberShapeComponent(shape = Shape.Pill)
    val indicatorRearComponent = rememberShapeComponent(shape = Shape.Pill)
    val indicator =
        rememberLayeredComponent(
            rear = indicatorRearComponent,
            front =
            rememberLayeredComponent(
                rear = indicatorCenterComponent,
                front = indicatorFrontComponent,
                padding = Dimensions.of(2.dp),
            ),
            padding = Dimensions.of(6.dp),
        )
    val guideline = rememberAxisGuidelineComponent(
        thickness = 2.dp,
        color = MaterialTheme.colorScheme.outlineVariant
    )
    val primaryColor = color.toArgb()
    val valueFormatter = remember {
        WeightTrackerMarkerValueFormatter(primaryColor)
    }
    return remember(label, indicator, guideline) {
        object :
            DefaultCartesianMarker(
                label = label,
                indicator = indicator,
                indicatorSizeDp = 24f,
                setIndicatorColor = { color ->
                    indicatorRearComponent.color = Color(color).copy(alpha = 0.15f).toArgb()
                    indicatorCenterComponent.color = color
                    indicatorCenterComponent.setShadow(radius = 8f, color = color)
                },
                guideline = guideline,
                valueFormatter = valueFormatter
            ) {
            override fun updateInsets(
                context: CartesianMeasureContext,
                horizontalDimensions: HorizontalDimensions,
                model: CartesianChartModel,
                insets: Insets
            ) {
                with(context) {
                    val baseShadowInsetDp =
                        CLIPPING_FREE_SHADOW_RADIUS_MULTIPLIER * LABEL_BACKGROUND_SHADOW_RADIUS_DP
                    var topInset = (baseShadowInsetDp - LABEL_BACKGROUND_SHADOW_DY_DP).pixels
                    var bottomInset = (baseShadowInsetDp + LABEL_BACKGROUND_SHADOW_DY_DP).pixels
                    when (labelPosition) {
                        LabelPosition.Top,
                        LabelPosition.AbovePoint ->
                            topInset += label.getHeight(context) + tickSizeDp.pixels

                        LabelPosition.Bottom ->
                            bottomInset += label.getHeight(context) + tickSizeDp.pixels

                        LabelPosition.AroundPoint -> {}
                    }
                    insets.ensureValuesAtLeast(top = topInset, bottom = bottomInset)
                }
            }
        }
    }
}

private const val LABEL_BACKGROUND_SHADOW_RADIUS_DP = 4f
private const val LABEL_BACKGROUND_SHADOW_DY_DP = 2f
private const val CLIPPING_FREE_SHADOW_RADIUS_MULTIPLIER = 1.4f