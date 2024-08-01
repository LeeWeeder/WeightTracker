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
import com.leeweeder.weighttracker.R
import com.leeweeder.weighttracker.ui.util.TextUtil
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

    var logToDelete by mutableStateOf<DeleteLogRequest?>(null)
        private set

    fun dismiss() {
        visible = false
    }

    fun show(logToDelete: DeleteLogRequest) {
        this.logToDelete = logToDelete
        visible = true
    }

    companion object {
        fun Saver(): Saver<ConfirmDeleteLogAlertDialogState, *> = Saver(
            save = {
                listOf(
                    it.visible,
                    it.logToDelete
                )
            },
            restore = {
                ConfirmDeleteLogAlertDialogState().apply {
                    visible = it[0] as Boolean
                    logToDelete = it[1] as DeleteLogRequest?
                }
            }
        )
    }
}

data class DeleteLogRequest(val id: Int, val date: LocalDate?)

@Composable
fun ConfirmDeleteLogAlertDialog(
    state: ConfirmDeleteLogAlertDialogState,
    onDismissRequest: () -> Unit,
    onConfirm: (DeleteLogRequest?) -> Unit
) {
    val text = state.logToDelete?.date?.let { TextUtil.confirmDialogTextBuilder("delete log", it) }
        ?: AnnotatedString("-")

    AlertDialog(
        visible = state.visible,
        onDismissRequest = onDismissRequest,
        title = "Confirm delete?",
        text = text,
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(state.logToDelete)
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