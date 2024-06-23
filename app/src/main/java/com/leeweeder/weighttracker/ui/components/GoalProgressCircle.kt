package com.leeweeder.weighttracker.ui.components

import android.graphics.RectF
import androidx.annotation.FloatRange
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.leeweeder.weighttracker.ui.theme.WeightTrackerTheme

@Composable
fun GoalProgressCircle(@FloatRange(from = 0.0, to = 1.0) progress: Float) {
    val trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
    val color = MaterialTheme.colorScheme.primary
    val dpToPx = @Composable { dp: Dp ->
        with(LocalDensity.current) {
            dp.toPx()
        }
    }
    val strokeWidth = dpToPx(8.dp)
    val canvasSize = dpToPx(64.dp)
    Canvas(modifier = Modifier.size(canvasSize.dp)) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val arcDiameter = minOf(canvasWidth, canvasHeight) * 0.9f
        val arcRadius = arcDiameter / 2f

        val rectF = RectF(
            canvasWidth / 2 - arcRadius,
            canvasHeight / 2 - arcRadius,
            canvasWidth / 2 + arcRadius,
            canvasHeight / 2 + arcRadius
        )

        val circle = 360f
        val sweepAngle = circle * 3 / 4
        val gap = circle - sweepAngle
        val startAngle = (circle / 4) + (gap / 2)

        val normalizedProgress = progress * sweepAngle

        val trackPathStartAngle: Float
        val trackPathSweepAngle: Float

        if (progress > 0f && progress < 1f) {
            trackPathStartAngle = startAngle + normalizedProgress + 8f
            trackPathSweepAngle = sweepAngle - (normalizedProgress + 8f)
        } else if (progress == 1f) {
            trackPathStartAngle =  startAngle + normalizedProgress
            trackPathSweepAngle = sweepAngle - normalizedProgress
        } else {
            trackPathStartAngle = startAngle
            trackPathSweepAngle = sweepAngle
        }

        val trackPath = Path().apply {
            arcTo(
                rect = Rect(
                    center.x - arcRadius,
                    center.y - arcRadius,
                    center.x + arcRadius,
                    center.y + arcRadius
                ),
                startAngleDegrees = trackPathStartAngle,
                sweepAngleDegrees = trackPathSweepAngle,
                forceMoveTo = false
            )
        }

        drawPath(
            color = trackColor,
            path = trackPath,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )

        drawArc(
            color = color,
            startAngle = startAngle,
            sweepAngle = normalizedProgress,
            useCenter = false,
            topLeft = Offset(rectF.left, rectF.top),
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
            size = Size(rectF.width(), rectF.height())
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun GoalProgressCirclePreview() {
    WeightTrackerTheme {
        GoalProgressCircle(.8f)
    }
}