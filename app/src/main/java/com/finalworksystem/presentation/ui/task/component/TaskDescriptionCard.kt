package com.finalworksystem.presentation.ui.task.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.finalworksystem.R
import com.finalworksystem.presentation.ui.component.BaseCard
import com.finalworksystem.presentation.ui.component.rememberHtmlText
import com.finalworksystem.ui.designsystem.AppSectionHeader
import com.finalworksystem.ui.designsystem.AppText
import com.finalworksystem.ui.theme.AppIcons
import com.finalworksystem.ui.theme.AppTheme

@Composable
fun TaskDescriptionCard(
    description: String,
    modifier: Modifier = Modifier
) {
    BaseCard(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AppSectionHeader(
                title = stringResource(R.string.description),
                icon = AppIcons.Article
            )

            AppText(
                text = rememberHtmlText(description),
                style = AppTheme.typography.bodyMedium,
                color = AppTheme.colors.onSurface
            )
        }
    }
}
