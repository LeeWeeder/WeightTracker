package com.leeweeder.weighttracker.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.leeweeder.weighttracker.R

@Composable
fun AlertDialog(visible: Boolean, onDismissRequest: () -> Unit, title: String, text: String) {
    if (visible) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = onDismissRequest,
            confirmButton = {
                TextButton(onClick = onDismissRequest) {
                    Text(text = "Okay")
                }
            },
            icon = {
                Icon(painter = painterResource(id = R.drawable.error), contentDescription = null)
            },
            title = {
                Text(text = title)
            },
            text = {
                Text(text = text)
            }
        )
    }
}