package com.finalworksystem.presentation.ui.component.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import com.finalworksystem.R
import com.finalworksystem.ui.designsystem.AppDrawerItem
import com.finalworksystem.ui.designsystem.AppDrawerState
import com.finalworksystem.ui.theme.AppIcons
import com.finalworksystem.ui.theme.AppTheme
import kotlinx.coroutines.launch

@Composable
fun LogoutSection(
    drawerState: AppDrawerState,
    onLogout: () -> Unit
) {
    val scope = rememberCoroutineScope()

    AppDrawerItem(
        label = stringResource(R.string.logout),
        icon = AppIcons.ExitToApp,
        tint = AppTheme.colors.error,
        onClick = {
            scope.launch {
                drawerState.close()
                onLogout()
            }
        }
    )
}
