package com.finalworksystem.presentation.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.finalworksystem.R
import com.finalworksystem.ui.designsystem.AppFab
import com.finalworksystem.ui.theme.AppIcons
import com.finalworksystem.ui.theme.AppTheme

@Composable
fun SearchResetFloatingActionButton(
    searchQuery: String,
    onSearchClick: () -> Unit,
    onResetClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.End,
        modifier = modifier
    ) {
        AnimatedVisibility(
            visible = searchQuery.isNotBlank(),
            enter = scaleIn(),
            exit = scaleOut()
        ) {
            AppFab(
                onClick = onResetClick,
                icon = AppIcons.Clear,
                contentDescription = stringResource(R.string.reset_search),
                containerColor = AppTheme.colors.surfaceElevated,
                contentColor = AppTheme.colors.error,
                small = true
            )
        }

        AppFab(
            onClick = onSearchClick,
            icon = AppIcons.Search,
            contentDescription = stringResource(R.string.search)
        )
    }
}
