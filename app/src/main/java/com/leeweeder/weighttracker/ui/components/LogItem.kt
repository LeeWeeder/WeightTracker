package com.leeweeder.weighttracker.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.leeweeder.weighttracker.domain.model.Log
import com.leeweeder.weighttracker.ui.util.relativeDate

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun LogItem(
    dateText: String,
    log: Log,
    relativeDateEnabled: Boolean = true,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val date = log.date
    ListItem(headlineContent = {
        Text(
            text = log.weight.displayValue + " kg",
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }, overlineContent = {
        Text(
            text = dateText,
            color = MaterialTheme.colorScheme.outline
        )
    }, modifier = Modifier.combinedClickable(onClick = onClick, onLongClick = onLongClick),
        trailingContent = if (relativeDateEnabled && date.relativeDate != null) {
            {
                Text(
                    text = date.relativeDate!!,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.8f)
                )
            }
        } else null
    )
}