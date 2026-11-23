package com.finalworksystem.presentation.ui.network

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.finalworksystem.R
import com.finalworksystem.ui.designsystem.AppButton
import com.finalworksystem.ui.designsystem.AppButtonVariant
import com.finalworksystem.ui.designsystem.AppEmptyState
import com.finalworksystem.ui.theme.AppIcons
import com.finalworksystem.ui.theme.AppTheme
import kotlinx.coroutines.launch

@Composable
fun NoConnectionScreen(
    onRetry: suspend () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    var isRetrying by remember { mutableStateOf(false) }

    AppEmptyState(
        title = stringResource(R.string.no_internet),
        message = stringResource(R.string.no_internet_description),
        icon = AppIcons.WifiOff,
        iconTint = AppTheme.colors.error,
        modifier = modifier
            .fillMaxSize()
            .background(AppTheme.colors.background),
        action = {
            AppButton(
                text = stringResource(R.string.try_again),
                onClick = {
                    coroutineScope.launch {
                        isRetrying = true
                        try {
                            onRetry()
                        } finally {
                            isRetrying = false
                        }
                    }
                },
                variant = AppButtonVariant.Primary,
                leadingIcon = AppIcons.Refresh,
                enabled = !isRetrying,
                loading = isRetrying
            )
        }
    )
}
