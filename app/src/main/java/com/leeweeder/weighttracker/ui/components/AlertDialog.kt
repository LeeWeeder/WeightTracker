package com.leeweeder.weighttracker.ui.components

import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import com.leeweeder.weighttracker.R
import com.leeweeder.weighttracker.ui.util.format
import java.time.LocalDate

@Composable
fun InvalidValueAlertDialog(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    title: String,
    text: String
) {
    AlertDialog(
        visible = visible,
        onDismissRequest = onDismissRequest,
        title = title,
        text = text,
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = "Okay")
            }
        },
        icon = {
            Icon(painter = painterResource(id = R.drawable.error), contentDescription = null)
        }
    )
}

@Composable
fun rememberConfirmDeleteLogAlertDialogState(): ConfirmDeleteLogAlertDialogState = rememberSaveable(
    saver = ConfirmDeleteLogAlertDialogState.Saver()
) {
    ConfirmDeleteLogAlertDialogState()
}

@Stable
class ConfirmDeleteLogAlertDialogState {
    var visible by mutableStateOf(false)
        private set

    var date by mutableStateOf<LocalDate?>(null)

    fun dismiss() {
        visible = false
    }

    fun show() {
        visible = true
    }

    companion object {
        fun Saver(): Saver<ConfirmDeleteLogAlertDialogState, *> = Saver(
            save = {
                listOf(
                    it.visible,
                    it.date
                )
            },
            restore = {
                ConfirmDeleteLogAlertDialogState().apply {
                    visible = it[0] as Boolean
                    date = it[1] as LocalDate?
                }
            }
        )
    }
}

@Composable
fun ConfirmDeleteLogAlertDialog(
    state: ConfirmDeleteLogAlertDialogState,
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit
) {
    val builder = AnnotatedString.Builder()
    builder.append("Are you sure you want to delete log for ")
    val date = state.date?.format("MM/d/yyyy") ?: "-"
    builder.withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
        append(date)
    }
    builder.append("? This operation cannot be undone.")

    val text = builder.toAnnotatedString()

    AlertDialog(
        visible = state.visible,
        onDismissRequest = onDismissRequest,
        title = "Confirm delete?",
        text = text,
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm()
                    onDismissRequest()
                },
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Text(text = "Delete")
            }
        },
        icon = {
            Icon(
                painter = painterResource(id = R.drawable.delete),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = "Cancel")
            }
        }
    )
}

@Composable
fun AlertDialog(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    title: String,
    text: AnnotatedString,
    confirmButton: @Composable () -> Unit,
    dismissButton: @Composable (() -> Unit)? = null,
    icon: @Composable (() -> Unit)? = null
) {
    if (visible) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = onDismissRequest,
            confirmButton = confirmButton,
            dismissButton = dismissButton,
            icon = icon,
            title = {
                Text(text = title)
            },
            text = {
                Text(text = text)
            }
        )
    }
}

@Composable
fun AlertDialog(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    title: String,
    text: String,
    confirmButton: @Composable () -> Unit,
    dismissButton: @Composable (() -> Unit)? = null,
    icon: @Composable (() -> Unit)? = null
) {
    if (visible) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = onDismissRequest,
            confirmButton = confirmButton,
            dismissButton = dismissButton,
            icon = icon,
            title = {
                Text(text = title)
            },
            text = {
                Text(text = text)
            }
        )
    }
}