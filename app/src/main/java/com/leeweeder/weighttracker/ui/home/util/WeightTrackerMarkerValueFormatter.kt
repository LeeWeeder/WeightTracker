package com.leeweeder.weighttracker.ui.home.util

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import com.leeweeder.weighttracker.ui.util.format
import com.patrykandpatrick.vico.core.cartesian.CartesianDrawContext
import com.patrykandpatrick.vico.core.cartesian.marker.CartesianMarker
import com.patrykandpatrick.vico.core.cartesian.marker.CartesianMarkerValueFormatter
import com.patrykandpatrick.vico.core.cartesian.marker.LineCartesianLayerMarkerTarget
import java.text.DecimalFormat
import java.time.LocalDate

class WeightTrackerMarkerValueFormatter(
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
                        append(" on " + LocalDate.ofEpochDay(point.entry.x.toLong()).format("MMM d"))
                        setSpan(StyleSpan(Typeface.BOLD), 0, this.length, 0)
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