package com.finalworksystem.presentation.ui.component.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import com.finalworksystem.R
import com.finalworksystem.domain.user.model.User
import com.finalworksystem.domain.user.model.UserRole
import com.finalworksystem.ui.designsystem.AppDrawerItem
import com.finalworksystem.ui.designsystem.AppDrawerSectionTitle
import com.finalworksystem.ui.designsystem.AppDrawerState
import com.finalworksystem.ui.theme.AppIcons
import kotlinx.coroutines.launch

@Composable
fun OpponentSection(
    user: User,
    drawerState: AppDrawerState,
    onNavigateToWorkList: (String) -> Unit
) {
    val scope = rememberCoroutineScope()

    if (user.roles.contains(UserRole.OPPONENT.value)) {
        AppDrawerSectionTitle(stringResource(R.string.opponent))

        AppDrawerItem(
            label = stringResource(R.string.opponent_work),
            icon = AppIcons.Article,
            onClick = {
                scope.launch {
                    drawerState.close()
                    onNavigateToWorkList("opponent")
                }
            }
        )
    }
}
