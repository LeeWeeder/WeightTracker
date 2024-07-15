package com.leeweeder.weighttracker.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.leeweeder.weighttracker.R

@Composable
internal fun NoData(modifier: Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.no_data),
            contentDescription = "No data available",
            alpha = 0.3f,
            modifier = Modifier.size(128.dp)
        )
        val style = MaterialTheme.typography.bodyMedium
        Text(
            text = "No data available",
            color = MaterialTheme.colorScheme.outlineVariant,
            style = style
        )
        Spacer(modifier = Modifier.height(2.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Click ", color = MaterialTheme.colorScheme.outline, style = style)
            Icon(
                painter = painterResource(id = R.drawable.add_weight_record),
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Text(text = " to add one", color = MaterialTheme.colorScheme.outline, style = style)
        }
    }
}