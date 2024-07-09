package com.leeweeder.weighttracker.ui.home.components

import android.text.Layout
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.leeweeder.weighttracker.ui.home.util.WeightTrackerMarkerValueFormatter
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisGuidelineComponent
import com.patrykandpatrick.vico.compose.common.component.fixed
import com.patrykandpatrick.vico.compose.common.component.rememberLayeredComponent
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.of
import com.patrykandpatrick.vico.compose.common.shape.markerCornered
import com.patrykandpatrick.vico.core.cartesian.CartesianMeasureContext
import com.patrykandpatrick.vico.core.cartesian.HorizontalDimensions
import com.patrykandpatrick.vico.core.cartesian.Insets
import com.patrykandpatrick.vico.core.cartesian.marker.CartesianMarker
import com.patrykandpatrick.vico.core.cartesian.marker.DefaultCartesianMarker
import com.patrykandpatrick.vico.core.common.Dimensions
import com.patrykandpatrick.vico.core.common.component.TextComponent
import com.patrykandpatrick.vico.core.common.shape.Corner
import com.patrykandpatrick.vico.core.common.shape.Shape

@Composable
internal fun rememberMarker(): CartesianMarker {
    val labelBackgroundShape = Shape.markerCornered(Corner.FullyRounded)
    val context = LocalContext.current
    val bgColor = Color(context.getColor(android.R.color.system_neutral1_10))
    val color = Color(context.getColor(android.R.color.system_accent1_400))
    val labelBackground =
        rememberShapeComponent(labelBackgroundShape, bgColor)
            .setShadow(
                radius = LABEL_BACKGROUND_SHADOW_RADIUS_DP,
                dy = LABEL_BACKGROUND_SHADOW_DY_DP,
                applyElevationOverlay = true,
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
        rememberShapeComponent(Shape.Pill, bgColor)
    val indicatorCenterComponent = rememberShapeComponent(Shape.Pill)
    val indicatorRearComponent = rememberShapeComponent(Shape.Pill)
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
                insets: Insets,
            ) {
                with(context) {
                    val baseShadowInsetDp =
                        CLIPPING_FREE_SHADOW_RADIUS_MULTIPLIER * LABEL_BACKGROUND_SHADOW_RADIUS_DP
                    var topInset = (baseShadowInsetDp - LABEL_BACKGROUND_SHADOW_DY_DP).pixels
                    var bottomInset = (baseShadowInsetDp + LABEL_BACKGROUND_SHADOW_DY_DP).pixels
                    when (labelPosition) {
                        LabelPosition.Top,
                        LabelPosition.AbovePoint ->
                            topInset += label.getHeight(context) + label.tickSizeDp.pixels

                        LabelPosition.Bottom ->
                            bottomInset += label.getHeight(context) + label.tickSizeDp.pixels

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