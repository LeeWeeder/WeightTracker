package com.leeweeder.weighttracker.ui.util

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import java.time.LocalDate

class TextUtil {
    companion object {
        fun confirmDialogTextBuilder(purpose: String, date: LocalDate): AnnotatedString {
            val builder = AnnotatedString.Builder()
            builder.append("Are you sure you want to ${purpose.lowercase()} for ")
            val formattedDate = date.format("MM/d/yyyy")
            builder.withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append(formattedDate)
            }
            builder.append("? This operation cannot be undone.")

            return builder.toAnnotatedString()
        }
    }
}