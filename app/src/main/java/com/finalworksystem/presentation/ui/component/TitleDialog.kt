package com.finalworksystem.presentation.ui.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.finalworksystem.R

@Composable
fun TitleDialog(
    title: String,
    isVisible: Boolean,
    onDismiss: () -> Unit
) {
    if (isVisible) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
            },
            confirmButton = {
                TextButton(
                    onClick = onDismiss
                ) {
                    Text(stringResource(R.string.ok))
                }
            }
        )
    }
}
