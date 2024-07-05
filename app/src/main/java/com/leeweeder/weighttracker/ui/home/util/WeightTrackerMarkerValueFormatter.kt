package com.leeweeder.weighttracker.ui.home.util

import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import com.leeweeder.weighttracker.ui.home.goalWeightKey
import com.patrykandpatrick.vico.core.cartesian.CartesianDrawContext
import com.patrykandpatrick.vico.core.cartesian.marker.CartesianMarker
import com.patrykandpatrick.vico.core.cartesian.marker.CartesianMarkerValueFormatter
import com.patrykandpatrick.vico.core.cartesian.marker.LineCartesianLayerMarkerTarget
import java.text.DecimalFormat

class WeightTrackerMarkerValueFormatter : CartesianMarkerValueFormatter {
    // There is only one chart. Edit as further feature is needed
    private fun SpannableStringBuilder.append(y: Float, color: Int) {
        val decimalFormat = DecimalFormat("#.##;−#.##")
        appendCompat(
            decimalFormat.format(y),
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
                        if (point.entry.y == context.chartValues.model.extraStore[goalWeightKey]) {
                            append("Goal achieved!", point.color)
                        } else {
                            append(point.entry.y, point.color)
                        }
                    }
                }
            }
        }
}

internal fun SpannableStringBuilder.appendCompat(
    text: CharSequence,
    what: Any,
    flags: Int,
): SpannableStringBuilder = append(text, what, flags)

fun SpannableStringBuilder.append(value: String, color: Int) {
    appendCompat(
        value,
        ForegroundColorSpan(color),
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE,
    )
}