package com.finalworksystem.presentation.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.finalworksystem.R
import com.finalworksystem.ui.designsystem.AppAlertDialog
import com.finalworksystem.ui.designsystem.AppButton
import com.finalworksystem.ui.designsystem.AppButtonVariant
import com.finalworksystem.ui.designsystem.AppText
import com.finalworksystem.ui.theme.AppTheme

@Composable
fun TitleDialog(
    title: String,
    isVisible: Boolean,
    onDismiss: () -> Unit
) {
    if (isVisible) {
        AppAlertDialog(
            onDismissRequest = onDismiss,
            title = {
                AppText(
                    text = title,
                    style = AppTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            },
            confirmButton = {
                AppButton(
                    text = stringResource(R.string.ok),
                    onClick = onDismiss,
                    variant = AppButtonVariant.Ghost
                )
            }
        )
    }
}
