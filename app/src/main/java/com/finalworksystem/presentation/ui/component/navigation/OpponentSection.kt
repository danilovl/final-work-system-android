package com.finalworksystem.presentation.ui.component.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Article
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import com.finalworksystem.R
import com.finalworksystem.domain.user.model.User
import com.finalworksystem.domain.user.model.UserRole
import kotlinx.coroutines.launch

@Composable
fun OpponentSection(
    user: User,
    drawerState: DrawerState,
    onNavigateToWorkList: (String) -> Unit
) {
    val scope = rememberCoroutineScope()

    if (user.roles.contains(UserRole.OPPONENT.value)) {
        NavigationDrawerItem(
            label = { Text(stringResource(R.string.opponent_work)) },
            icon = { 
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.Article,
                    contentDescription = stringResource(R.string.opponent_work)
                )
            },
            selected = false,
            onClick = {
                scope.launch {
                    drawerState.close()
                    onNavigateToWorkList("opponent")
                }
            }
        )
    }
}