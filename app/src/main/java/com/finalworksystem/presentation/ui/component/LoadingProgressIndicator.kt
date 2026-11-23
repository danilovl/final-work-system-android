package com.finalworksystem.presentation.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.finalworksystem.ui.designsystem.AppSpinner
import com.finalworksystem.ui.designsystem.AppText
import com.finalworksystem.ui.theme.AppTheme

@Composable
fun LoadingProgressIndicator(
    loadedCount: Int,
    totalCount: Int = -1,
    isLoading: Boolean = false
) {
    when {
        isLoading && loadedCount == 0 -> {
            Box(
                modifier = Modifier.size(32.dp),
                contentAlignment = Alignment.Center
            ) {
                AppSpinner(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
            }
        }
        else -> {
            AppText(
                text = "$loadedCount/$totalCount",
                style = AppTheme.typography.labelMedium,
                color = AppTheme.colors.onPrimaryContainer,
                modifier = Modifier
                    .background(AppTheme.colors.primaryContainer, CircleShape)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            )
        }
    }
}
